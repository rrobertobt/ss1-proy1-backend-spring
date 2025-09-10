package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.promotion.PromotionListResponseDto;
import usac.cunoc.bpmn.entity.CdPromotion;
import usac.cunoc.bpmn.entity.CdPromotionArticle;
import usac.cunoc.bpmn.repository.CdPromotionArticleRepository;
import usac.cunoc.bpmn.repository.CdPromotionRepository;
import usac.cunoc.bpmn.repository.CdRepository;
import usac.cunoc.bpmn.service.PromotionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Promotion service implementation for public promotion operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionServiceImpl implements PromotionService {

    private final CdPromotionRepository cdPromotionRepository;
    private final CdPromotionArticleRepository cdPromotionArticleRepository;
    private final CdRepository cdRepository;

    @Override
    public PromotionListResponseDto getCdPromotions(String type, Integer genreId, Boolean isActive) {
        log.info("Getting CD promotions with filters - type: {}, genreId: {}, isActive: {}",
                type, genreId, isActive);

        List<CdPromotion> promotions;

        if (type != null) {
            switch (type.toLowerCase()) {
                case "por genero":
                    if (genreId != null) {
                        promotions = cdPromotionRepository.findActivePromotionsByGenre(genreId, LocalDateTime.now());
                    } else {
                        promotions = cdPromotionRepository.findActivePromotions(LocalDateTime.now())
                                .stream()
                                .filter(p -> p.getMusicGenre() != null)
                                .collect(Collectors.toList());
                    }
                    break;
                case "aleatorio":
                    promotions = cdPromotionRepository.findActiveRandomPromotions(LocalDateTime.now());
                    break;
                default:
                    promotions = cdPromotionRepository.findActivePromotions(LocalDateTime.now());
            }
        } else {
            promotions = cdPromotionRepository.findActivePromotions(LocalDateTime.now());
        }

        // Apply active filter if specified
        if (isActive != null) {
            promotions = promotions.stream()
                    .filter(p -> p.getIsActive().equals(isActive))
                    .collect(Collectors.toList());
        }

        List<PromotionListResponseDto.PromotionDto> promotionDtos = promotions.stream()
                .map(this::mapToPromotionDto)
                .collect(Collectors.toList());

        log.info("Found {} CD promotions", promotionDtos.size());
        return new PromotionListResponseDto(promotionDtos);
    }

    private PromotionListResponseDto.PromotionDto mapToPromotionDto(CdPromotion promotion) {
        // Map promotion type
        PromotionListResponseDto.PromotionTypeDto promotionTypeDto = new PromotionListResponseDto.PromotionTypeDto(
                promotion.getCdPromotionType().getId(),
                promotion.getCdPromotionType().getName(),
                promotion.getCdPromotionType().getMaxItems(),
                promotion.getCdPromotionType().getDiscountPercentage());

        // Map genre (only for genre-based promotions)
        PromotionListResponseDto.GenreDto genreDto = null;
        if (promotion.getMusicGenre() != null) {
            genreDto = new PromotionListResponseDto.GenreDto(
                    promotion.getMusicGenre().getId(),
                    promotion.getMusicGenre().getName());
        }

        // Get eligible articles
        List<CdPromotionArticle> promotionArticles = cdPromotionArticleRepository
                .findByPromotionId(promotion.getId());

        List<PromotionListResponseDto.EligibleArticleDto> eligibleArticles = promotionArticles.stream()
                .filter(pa -> cdRepository.existsByAnalogArticleId(pa.getAnalogArticle().getId()))
                .map(pa -> new PromotionListResponseDto.EligibleArticleDto(
                        pa.getAnalogArticle().getId(),
                        pa.getAnalogArticle().getTitle(),
                        pa.getAnalogArticle().getArtist().getName(),
                        pa.getAnalogArticle().getPrice(),
                        pa.getAnalogArticle().getImageUrl()))
                .collect(Collectors.toList());

        return new PromotionListResponseDto.PromotionDto(
                promotion.getId(),
                promotion.getName(),
                promotionTypeDto,
                genreDto,
                promotion.getDiscountPercentage(),
                promotion.getMaxItems(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getIsActive(),
                promotion.getUsageCount(),
                eligibleArticles);
    }
}