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
import usac.cunoc.bpmn.service.CatalogService;

/**
 * Catalog controller for customer-facing catalog operations - matches PDF
 * specification exactly
 */
@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Customer catalog browsing and search operations")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/articles")
    @Operation(summary = "Get articles catalog", description = "Get paginated list of available articles with filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<ApiResponseDto<CatalogArticlesResponseDto>> getArticles(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Items per page") @RequestParam(defaultValue = "12") Integer limit,
            @Parameter(description = "Search term for title or artist") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by genre ID") @RequestParam(required = false) Integer genreId,
            @Parameter(description = "Filter by artist ID") @RequestParam(required = false) Integer artistId,
            @Parameter(description = "Minimum price") @RequestParam(required = false) String minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) String maxPrice,
            @Parameter(description = "Filter by type") @RequestParam(required = false) String type,
            @Parameter(description = "Sort criteria") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Filter by availability") @RequestParam(required = false) Boolean availability) {

        CatalogArticlesResponseDto articles = catalogService.getArticles(
                page, limit, search, genreId, artistId, minPrice, maxPrice, type, sortBy, availability);

        return ResponseEntity.ok(ApiResponseDto.success(articles));
    }

    @GetMapping("/articles/{id}")
    @Operation(summary = "Get article details", description = "Get detailed information about a specific article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ResponseEntity<ApiResponseDto<ArticleDetailResponseDto>> getArticleById(
            @Parameter(description = "Article ID") @PathVariable Integer id) {

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
            @Parameter(description = "Article ID") @PathVariable Integer id,
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Items per page") @RequestParam(defaultValue = "10") Integer limit) {

        ArticleCommentsResponseDto comments = catalogService.getArticleComments(id, page, limit);
        return ResponseEntity.ok(ApiResponseDto.success(comments));
    }

    @GetMapping("/articles/{id}/ratings")
    @Operation(summary = "Get article ratings", description = "Get paginated ratings for a specific article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ratings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ResponseEntity<ApiResponseDto<ArticleRatingsResponseDto>> getArticleRatings(
            @Parameter(description = "Article ID") @PathVariable Integer id,
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Items per page") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "Sort criteria") @RequestParam(required = false) String sortBy) {

        ArticleRatingsResponseDto ratings = catalogService.getArticleRatings(id, page, limit, sortBy);
        return ResponseEntity.ok(ApiResponseDto.success(ratings));
    }
}