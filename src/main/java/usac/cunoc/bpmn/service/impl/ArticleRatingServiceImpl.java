package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.rating.CreateRatingRequestDto;
import usac.cunoc.bpmn.dto.rating.CreateRatingResponseDto;
import usac.cunoc.bpmn.dto.rating.DeleteRatingResponseDto;
import usac.cunoc.bpmn.dto.rating.UpdateRatingRequestDto;
import usac.cunoc.bpmn.dto.rating.UpdateRatingResponseDto;
import usac.cunoc.bpmn.entity.AnalogArticle;
import usac.cunoc.bpmn.entity.ArticleRating;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.AnalogArticleRepository;
import usac.cunoc.bpmn.repository.ArticleRatingRepository;
import usac.cunoc.bpmn.repository.OrderItemRepository;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.ArticleRatingService;

import java.time.LocalDateTime;

/**
 * Implementation of ArticleRatingService - handles all article rating
 * operations
 * Includes business validation and verified purchase checking
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ArticleRatingServiceImpl implements ArticleRatingService {

    private final ArticleRatingRepository articleRatingRepository;
    private final AnalogArticleRepository analogArticleRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public CreateRatingResponseDto createRating(Integer articleId, CreateRatingRequestDto request, Integer userId) {
        log.info("Creating rating for article: {} by user: {}", articleId, userId);

        // Validate article exists
        AnalogArticle article = analogArticleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Check if user already rated this article
        if (articleRatingRepository.existsByAnalogArticleIdAndUserId(articleId, userId)) {
            throw new RuntimeException("Ya has calificado este artículo. Puedes actualizar tu calificación existente.");
        }

        // Check if it's a verified purchase
        Boolean isVerifiedPurchase = checkVerifiedPurchase(articleId, userId);

        // Create new rating
        ArticleRating rating = new ArticleRating();
        rating.setAnalogArticle(article);
        rating.setUser(user);
        rating.setRating(request.getRating());
        rating.setReviewText(request.getReviewText());
        rating.setIsVerifiedPurchase(isVerifiedPurchase);

        // Save rating
        ArticleRating savedRating = articleRatingRepository.save(rating);

        log.info("Rating created successfully with ID: {}", savedRating.getId());

        return new CreateRatingResponseDto(
                savedRating.getId(),
                articleId,
                userId,
                savedRating.getRating(),
                savedRating.getReviewText(),
                savedRating.getIsVerifiedPurchase(),
                savedRating.getCreatedAt());
    }

    @Override
    @Transactional
    public UpdateRatingResponseDto updateRating(Integer articleId, Integer ratingId,
            UpdateRatingRequestDto request, Integer userId) {
        log.info("Updating rating: {} for article: {} by user: {}", ratingId, articleId, userId);

        // Find existing rating
        ArticleRating rating = articleRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));

        // Validate ownership and article match
        if (!rating.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar esta calificación");
        }

        if (!rating.getAnalogArticle().getId().equals(articleId)) {
            throw new RuntimeException("La calificación no pertenece a este artículo");
        }

        // Update rating
        rating.setRating(request.getRating());
        rating.setReviewText(request.getReviewText());

        // Save updated rating
        ArticleRating updatedRating = articleRatingRepository.save(rating);

        log.info("Rating updated successfully: {}", ratingId);

        return new UpdateRatingResponseDto(
                updatedRating.getId(),
                updatedRating.getRating(),
                updatedRating.getReviewText(),
                updatedRating.getUpdatedAt());
    }

    @Override
    @Transactional
    public DeleteRatingResponseDto deleteRating(Integer articleId, Integer ratingId, Integer userId) {
        log.info("Deleting rating: {} for article: {} by user: {}", ratingId, articleId, userId);

        // Find existing rating
        ArticleRating rating = articleRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));

        // Validate ownership and article match
        if (!rating.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta calificación");
        }

        if (!rating.getAnalogArticle().getId().equals(articleId)) {
            throw new RuntimeException("La calificación no pertenece a este artículo");
        }

        // Delete rating
        articleRatingRepository.delete(rating);

        LocalDateTime deletedAt = LocalDateTime.now();
        log.info("Rating deleted successfully: {}", ratingId);

        return new DeleteRatingResponseDto(
                ratingId,
                articleId,
                true,
                deletedAt);
    }

    // PRIVATE HELPER METHODS

    /**
     * Check if user has purchased this article (verified purchase)
     */
    private Boolean checkVerifiedPurchase(Integer articleId, Integer userId) {
        try {
            // Check if user has successfully purchased this article
            // Looking for delivered orders (order_status_id = 4)
            return orderItemRepository.existsByUserIdAndArticleIdAndDelivered(userId, articleId);
        } catch (Exception e) {
            log.warn("Error checking verified purchase for user: {} and article: {}", userId, articleId);
            return false;
        }
    }
}