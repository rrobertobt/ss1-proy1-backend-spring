package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.promotion.CreatePromotionRequestDto;
import usac.cunoc.bpmn.dto.promotion.CreatePromotionResponseDto;
import usac.cunoc.bpmn.dto.promotion.UpdatePromotionRequestDto;
import usac.cunoc.bpmn.dto.promotion.UpdatePromotionResponseDto;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.AdminPromotionService;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin promotion service implementation - handles administrative promotion
 * operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPromotionServiceImpl implements AdminPromotionService {

    private final CdPromotionRepository cdPromotionRepository;
    private final CdPromotionTypeRepository cdPromotionTypeRepository;
    private final CdPromotionArticleRepository cdPromotionArticleRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final AnalogArticleRepository analogArticleRepository;
    private final CdRepository cdRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CreatePromotionResponseDto createCdPromotion(CreatePromotionRequestDto request, Integer adminUserId) {
        log.info("Creating CD promotion: {} by admin user ID: {}", request.getName(), adminUserId);

        // Validate admin user exists
        userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));

        // Validate promotion type exists
        CdPromotionType promotionType = cdPromotionTypeRepository.findById(request.getPromotionTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de promoción no encontrado"));

        // Validate genre for genre-based promotions
        MusicGenre genre = null;
        if (request.getGenreId() != null) {
            genre = musicGenreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new RuntimeException("Género musical no encontrado"));
        }

        // Validate promotion type requirements
        validatePromotionTypeRequirements(promotionType, request, genre);

        // Validate all articles exist and are CDs
        validateArticlesForPromotion(request.getArticleIds(), genre);

        // Create promotion
        CdPromotion promotion = new CdPromotion();
        promotion.setName(request.getName());
        promotion.setCdPromotionType(promotionType);
        promotion.setMusicGenre(genre);
        promotion.setDiscountPercentage(request.getDiscountPercentage());
        promotion.setMaxItems(request.getMaxItems());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(true);
        promotion.setUsageCount(0);

        CdPromotion savedPromotion = cdPromotionRepository.save(promotion);
        log.info("Created CD promotion with ID: {}", savedPromotion.getId());

        // Create promotion-article associations
        createPromotionArticleAssociations(savedPromotion, request.getArticleIds());

        // Build response
        CreatePromotionResponseDto.PromotionTypeDto promotionTypeDto = new CreatePromotionResponseDto.PromotionTypeDto(
                promotionType.getId(),
                promotionType.getName());

        return new CreatePromotionResponseDto(
                savedPromotion.getId(),
                savedPromotion.getName(),
                promotionTypeDto,
                savedPromotion.getDiscountPercentage(),
                savedPromotion.getMaxItems(),
                savedPromotion.getStartDate(),
                savedPromotion.getEndDate(),
                savedPromotion.getIsActive(),
                savedPromotion.getCreatedAt());
    }

    @Override
    @Transactional
    public UpdatePromotionResponseDto updateCdPromotion(Integer promotionId, UpdatePromotionRequestDto request,
            Integer adminUserId) {
        log.info("Updating CD promotion ID: {} by admin user ID: {}", promotionId, adminUserId);

        // Validate admin user exists
        userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));

        // Find existing promotion
        CdPromotion promotion = cdPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

        // Validate articles for promotion update
        validateArticlesForPromotion(request.getArticleIds(), promotion.getMusicGenre());

        // Update promotion fields
        promotion.setName(request.getName());
        promotion.setDiscountPercentage(request.getDiscountPercentage());
        promotion.setMaxItems(request.getMaxItems());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(request.getIsActive());
        promotion.setUpdatedAt(LocalDateTime.now());

        CdPromotion updatedPromotion = cdPromotionRepository.save(promotion);

        // Update promotion-article associations
        updatePromotionArticleAssociations(promotion, request.getArticleIds());

        log.info("Updated CD promotion ID: {}", promotionId);

        return new UpdatePromotionResponseDto(
                updatedPromotion.getId(),
                updatedPromotion.getName(),
                updatedPromotion.getUpdatedAt());
    }

    @Override
    @Transactional
    public void deleteCdPromotion(Integer promotionId, Integer adminUserId) {
        log.info("Deleting CD promotion ID: {} by admin user ID: {}", promotionId, adminUserId);

        // Validate admin user exists
        userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));

        // Validate promotion exists
        CdPromotion promotion = cdPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

        // Delete promotion-article associations first (due to foreign key constraints)
        cdPromotionArticleRepository.deleteByPromotionId(promotionId);

        // Delete the promotion
        cdPromotionRepository.delete(promotion);

        log.info("Deleted CD promotion ID: {}", promotionId);
    }

    private void validatePromotionTypeRequirements(CdPromotionType promotionType, CreatePromotionRequestDto request,
            MusicGenre genre) {
        // Validate max items according to promotion type
        if (request.getMaxItems() > promotionType.getMaxItems()) {
            throw new RuntimeException("La promoción de tipo '" + promotionType.getName() +
                    "' permite máximo " + promotionType.getMaxItems() + " artículos");
        }

        // Validate articles count
        if (request.getArticleIds().size() > request.getMaxItems()) {
            throw new RuntimeException("El número de artículos (" + request.getArticleIds().size() +
                    ") excede el máximo permitido (" + request.getMaxItems() + ")");
        }

        // Validate genre requirement for genre-based promotions
        if ("Por Genero".equals(promotionType.getName()) && genre == null) {
            throw new RuntimeException("Las promociones por género requieren especificar un género");
        }

        // Validate no genre for random promotions
        if ("Aleatorio".equals(promotionType.getName()) && genre != null) {
            throw new RuntimeException("Las promociones aleatorias no deben especificar un género");
        }

        // Validate time limitations
        if (promotionType.getIsTimeLimited() && request.getEndDate() == null) {
            throw new RuntimeException("Las promociones de tipo '" + promotionType.getName() +
                    "' requieren fecha de finalización");
        }
    }

    private void validateArticlesForPromotion(List<Integer> articleIds, MusicGenre requiredGenre) {
        for (Integer articleId : articleIds) {
            // Validate article exists
            AnalogArticle article = analogArticleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("Artículo ID " + articleId + " no encontrado"));

            // Validate article is a CD
            if (!cdRepository.existsByAnalogArticleId(articleId)) {
                throw new RuntimeException("El artículo '" + article.getTitle() + "' no es un CD");
            }

            // Validate genre for genre-based promotions
            if (requiredGenre != null && !article.getMusicGenre().getId().equals(requiredGenre.getId())) {
                throw new RuntimeException("El CD '" + article.getTitle() +
                        "' no pertenece al género '" + requiredGenre.getName() + "'");
            }

            // Validate article is available
            if (!article.getIsAvailable()) {
                throw new RuntimeException("El artículo '" + article.getTitle() + "' no está disponible");
            }
        }
    }

    private void createPromotionArticleAssociations(CdPromotion promotion, List<Integer> articleIds) {
        for (Integer articleId : articleIds) {
            AnalogArticle article = analogArticleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("Artículo ID " + articleId + " no encontrado"));

            CdPromotionArticleId id = new CdPromotionArticleId(promotion.getId(), articleId);
            CdPromotionArticle promotionArticle = new CdPromotionArticle();
            promotionArticle.setId(id);
            promotionArticle.setCdPromotion(promotion);
            promotionArticle.setAnalogArticle(article);

            cdPromotionArticleRepository.save(promotionArticle);
        }
        log.info("Created {} promotion-article associations for promotion ID: {}",
                articleIds.size(), promotion.getId());
    }

    private void updatePromotionArticleAssociations(CdPromotion promotion, List<Integer> articleIds) {
        // Delete existing associations
        cdPromotionArticleRepository.deleteByPromotionId(promotion.getId());

        // Create new associations
        createPromotionArticleAssociations(promotion, articleIds);

        log.info("Updated promotion-article associations for promotion ID: {}", promotion.getId());
    }
}