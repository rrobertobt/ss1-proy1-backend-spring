package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.catalog.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.enums.ArticleType;
import usac.cunoc.bpmn.service.CatalogService;

/**
 * Catalog controller for customer-facing catalog operations
 * Enhanced with comprehensive type filtering support
 */
@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Customer catalog browsing and search operations")
public class CatalogController {

        private final CatalogService catalogService;

        @GetMapping("/articles")
        @Operation(summary = "Get articles catalog with filters", description = "Get paginated list of available articles with comprehensive filtering options including type, genre, artist, price range and search")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid parameters")
        })
        public ResponseEntity<ApiResponseDto<CatalogArticlesResponseDto>> getArticles(
                        @Parameter(description = "Page number (1-based)", example = "1") @RequestParam(defaultValue = "1") Integer page,

                        @Parameter(description = "Items per page (use 0 for all)", example = "12") @RequestParam(defaultValue = "12") Integer limit,

                        @Parameter(description = "Search term for title or artist name", example = "Beatles") @RequestParam(required = false) String search,

                        @Parameter(description = "Filter by music genre ID", example = "1") @RequestParam(required = false) Integer genreId,

                        @Parameter(description = "Filter by artist ID", example = "5") @RequestParam(required = false) Integer artistId,

                        @Parameter(description = "Minimum price filter", example = "10.00") @RequestParam(required = false) String minPrice,

                        @Parameter(description = "Maximum price filter", example = "50.00") @RequestParam(required = false) String maxPrice,

                        @Parameter(description = "Filter by article type: vinyl, cassette, or cd", example = "vinyl") @RequestParam(required = false) String type,

                        @Parameter(description = "Sort criteria: price_asc, price_desc, rating, popular, name_asc, name_desc", example = "price_asc") @RequestParam(required = false) String sortBy,

                        @Parameter(description = "Filter by availability status", example = "true") @RequestParam(required = false) Boolean availability) {

                // Validate article type parameter
                if (type != null && !type.trim().isEmpty() && !ArticleType.isValidCode(type)) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponseDto.error(
                                                        "Invalid article type. Allowed values: vinyl, cassette, cd"));
                }

                CatalogArticlesResponseDto articles = catalogService.getArticles(
                                page, limit, search, genreId, artistId, minPrice, maxPrice, type, sortBy, availability);

                return ResponseEntity.ok(ApiResponseDto.success(articles));
        }

        @GetMapping("/articles/{id}")
        @Operation(summary = "Get article details", description = "Get detailed information about a specific article including type-specific details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Article details retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Article not found")
        })
        public ResponseEntity<ApiResponseDto<ArticleDetailResponseDto>> getArticleById(
                        @Parameter(description = "Article ID", example = "1") @PathVariable Integer id) {

                ArticleDetailResponseDto article = catalogService.getArticleById(id);
                return ResponseEntity.ok(ApiResponseDto.success(article));
        }

        @GetMapping("/articles/{id}/comments")
        @Operation(summary = "Get article comments", description = "Get paginated comments for a specific article")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Article not found")
        })
        public ResponseEntity<ApiResponseDto<ArticleCommentsResponseDto>> getArticleComments(
                        @Parameter(description = "Article ID", example = "1") @PathVariable Integer id,
                        @Parameter(description = "Page number (1-based)", example = "1") @RequestParam(defaultValue = "1") Integer page,
                        @Parameter(description = "Items per page", example = "10") @RequestParam(defaultValue = "10") Integer limit) {

                ArticleCommentsResponseDto comments = catalogService.getArticleComments(id, page, limit);
                return ResponseEntity.ok(ApiResponseDto.success(comments));
        }

        @GetMapping("/articles/{id}/ratings")
        @Operation(summary = "Get article ratings", description = "Get paginated ratings and reviews for a specific article")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ratings retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Article not found")
        })
        public ResponseEntity<ApiResponseDto<ArticleRatingsResponseDto>> getArticleRatings(
                        @Parameter(description = "Article ID", example = "1") @PathVariable Integer id,
                        @Parameter(description = "Page number (1-based)", example = "1") @RequestParam(defaultValue = "1") Integer page,
                        @Parameter(description = "Items per page", example = "10") @RequestParam(defaultValue = "10") Integer limit,
                        @Parameter(description = "Sort by: newest, oldest, helpful, rating") @RequestParam(required = false) String sortBy) {

                ArticleRatingsResponseDto ratings = catalogService.getArticleRatings(id, page, limit, sortBy);
                return ResponseEntity.ok(ApiResponseDto.success(ratings));
        }
}
