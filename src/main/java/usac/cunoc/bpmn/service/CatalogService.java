package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.catalog.*;

/**
 * Catalog service interface for customer-facing catalog operations
 */
public interface CatalogService {

    /**
     * Get paginated list of articles with filters
     */
    CatalogArticlesResponseDto getArticles(
            Integer page,
            Integer limit,
            String search,
            Integer genreId,
            Integer artistId,
            String minPrice,
            String maxPrice,
            String type,
            String sortBy,
            Boolean availability);

    /**
     * Get article details by ID
     */
    ArticleDetailResponseDto getArticleById(Integer articleId);

    /**
     * Get article comments with pagination
     */
    ArticleCommentsResponseDto getArticleComments(Integer articleId, Integer page, Integer limit);

    /**
     * Get article ratings with pagination
     */
    ArticleRatingsResponseDto getArticleRatings(Integer articleId, Integer page, Integer limit, String sortBy);
}