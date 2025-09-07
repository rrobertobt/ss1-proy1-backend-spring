package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.admin.catalog.*;
import usac.cunoc.bpmn.dto.catalog.ArticleDetailResponseDto;
import usac.cunoc.bpmn.dto.catalog.CatalogArticlesResponseDto;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminCatalogService;
import java.time.LocalDate;

/**
 * Admin catalog controller for administrative catalog operations - matches PDF
 * specification exactly
 */
@RestController
@RequestMapping("/api/v1/admin/catalog")
@RequiredArgsConstructor
@Tag(name = "Admin Catalog Management", description = "Administrative catalog and inventory operations")
public class AdminCatalogController {

    private final AdminCatalogService adminCatalogService;
    private final UserRepository userRepository;

    @PostMapping("/articles")
    @Operation(summary = "Create new article", description = "Create a new vinyl, cassette, or CD article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Article created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<CreateArticleResponseDto>> createArticle(
            @Valid @RequestBody CreateArticleRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        CreateArticleResponseDto response = adminCatalogService.createArticle(request, adminUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Artículo creado exitosamente", response));
    }

    @GetMapping("/articles")
    @Operation(summary = "Get articles for admin", description = "Get paginated list of articles for administrative management")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<CatalogArticlesResponseDto>> getArticlesForAdmin(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Items per page") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by genre ID") @RequestParam(required = false) Integer genreId,
            @Parameter(description = "Filter by artist ID") @RequestParam(required = false) Integer artistId,
            @Parameter(description = "Filter by type") @RequestParam(required = false) String type,
            @Parameter(description = "Filter by availability") @RequestParam(required = false) Boolean isAvailable) {

        CatalogArticlesResponseDto articles = adminCatalogService.getArticlesForAdmin(
                page, limit, search, genreId, artistId, type, isAvailable);

        return ResponseEntity.ok(ApiResponseDto.success(articles));
    }

    @GetMapping("/articles/{id}")
    @Operation(summary = "Get article details", description = "Get detailed article information for admin view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<ArticleDetailResponseDto>> getArticleById(
            @Parameter(description = "Article ID") @PathVariable Integer id) {

        ArticleDetailResponseDto article = adminCatalogService.getArticleById(id);
        return ResponseEntity.ok(ApiResponseDto.success(article));
    }

    @PutMapping("/articles/{id}")
    @Operation(summary = "Update article", description = "Update an existing article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<ArticleDetailResponseDto>> updateArticle(
            @Parameter(description = "Article ID") @PathVariable Integer id,
            @Valid @RequestBody CreateArticleRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        ArticleDetailResponseDto response = adminCatalogService.updateArticle(id, request, adminUserId);

        return ResponseEntity.ok(ApiResponseDto.success("Artículo actualizado exitosamente", response));
    }

    @DeleteMapping("/articles/{id}")
    @Operation(summary = "Delete article", description = "Soft delete an article (mark as unavailable)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<Object>> deleteArticle(
            @Parameter(description = "Article ID") @PathVariable Integer id,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        adminCatalogService.deleteArticle(id, adminUserId);

        return ResponseEntity.ok(ApiResponseDto.success("Artículo eliminado exitosamente", null));
    }

    @PostMapping("/stock/{id}")
    @Operation(summary = "Create stock movement", description = "Create a stock entry or exit movement for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stock movement created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<StockMovementResponseDto>> createStockMovement(
            @Parameter(description = "Article ID") @PathVariable Integer id,
            @Valid @RequestBody StockMovementRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        StockMovementResponseDto response = adminCatalogService.createStockMovement(id, request, adminUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Movimiento de inventario registrado exitosamente", response));
    }

    @GetMapping("/stock-movements")
    @Operation(summary = "Get stock movements", description = "Get paginated list of stock movements with filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock movements retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<StockMovementListResponseDto>> getStockMovements(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Items per page") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "Filter by article ID") @RequestParam(required = false) Integer articleId,
            @Parameter(description = "Filter by movement type") @RequestParam(required = false) String movementType,
            @Parameter(description = "Filter from date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @Parameter(description = "Filter to date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        StockMovementListResponseDto movements = adminCatalogService.getStockMovements(
                page, limit, articleId, movementType, dateFrom, dateTo);

        return ResponseEntity.ok(ApiResponseDto.success(movements));
    }

    // PRIVATE HELPER METHODS

    private Integer getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String username = authentication.getName();
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
}