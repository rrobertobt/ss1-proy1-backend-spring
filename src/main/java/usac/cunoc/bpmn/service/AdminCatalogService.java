package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.admin.catalog.*;
import usac.cunoc.bpmn.dto.catalog.ArticleDetailResponseDto;
import usac.cunoc.bpmn.dto.catalog.CatalogArticlesResponseDto;
import java.time.LocalDate;

/**
 * Admin catalog service interface for administrative catalog operations
 */
public interface AdminCatalogService {

    /**
     * Create a new article (vinyl, cassette, or CD)
     */
    CreateArticleResponseDto createArticle(CreateArticleRequestDto request, Integer adminUserId);

    /**
     * Get article details by ID (admin view)
     */
    ArticleDetailResponseDto getArticleById(Integer articleId);

    /**
     * Update an existing article
     */
    ArticleDetailResponseDto updateArticle(Integer articleId, CreateArticleRequestDto request, Integer adminUserId);

    /**
     * Delete an article (soft delete)
     */
    void deleteArticle(Integer articleId, Integer adminUserId);

    /**
     * Get paginated list of articles for admin management
     */
    CatalogArticlesResponseDto getArticlesForAdmin(
            Integer page,
            Integer limit,
            String search,
            Integer genreId,
            Integer artistId,
            String type,
            Boolean isAvailable);

    /**
     * Create stock movement (entry or exit)
     */
    StockMovementResponseDto createStockMovement(
            Integer articleId,
            StockMovementRequestDto request,
            Integer adminUserId);

    /**
     * Get stock movements history with filters
     */
    StockMovementListResponseDto getStockMovements(
            Integer page,
            Integer limit,
            Integer articleId,
            String movementType,
            LocalDate dateFrom,
            LocalDate dateTo);
}