package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.rating.CreateRatingRequestDto;
import usac.cunoc.bpmn.dto.rating.CreateRatingResponseDto;
import usac.cunoc.bpmn.dto.rating.DeleteRatingResponseDto;
import usac.cunoc.bpmn.dto.rating.UpdateRatingRequestDto;
import usac.cunoc.bpmn.dto.rating.UpdateRatingResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.ArticleRatingService;

/**
 * Article rating controller for rating and review operations - matches PDF
 * specification exactly
 * Handles all article rating endpoints: create, update, and delete ratings
 */
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Tag(name = "Article Ratings", description = "Article rating and review management operations")
public class ArticleRatingController {

    private final ArticleRatingService articleRatingService;
    private final UserRepository userRepository;

    @PostMapping("/{id}/ratings")
    @Operation(summary = "Create article rating", description = "Create a new rating and review for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating data or user already rated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article or user not found")
    })
    public ResponseEntity<ApiResponseDto<CreateRatingResponseDto>> createRating(
            @PathVariable @Parameter(description = "Article ID") Integer id,
            @Valid @RequestBody CreateRatingRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        CreateRatingResponseDto response = articleRatingService.createRating(id, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Calificación agregada exitosamente", response));
    }

    @PutMapping("/{articleId}/ratings/{id}")
    @Operation(summary = "Update article rating", description = "Update an existing rating and review for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not owner of rating"),
            @ApiResponse(responseCode = "404", description = "Rating, article or user not found")
    })
    public ResponseEntity<ApiResponseDto<UpdateRatingResponseDto>> updateRating(
            @PathVariable @Parameter(description = "Article ID") Integer articleId,
            @PathVariable @Parameter(description = "Rating ID") Integer id,
            @Valid @RequestBody UpdateRatingRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        UpdateRatingResponseDto response = articleRatingService.updateRating(articleId, id, request, userId);

        return ResponseEntity.ok(
                ApiResponseDto.success("Calificación actualizada exitosamente", response));
    }

    @DeleteMapping("/{articleId}/ratings/{id}")
    @Operation(summary = "Delete article rating", description = "Delete an existing rating and review for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not owner of rating"),
            @ApiResponse(responseCode = "404", description = "Rating, article or user not found")
    })
    public ResponseEntity<ApiResponseDto<DeleteRatingResponseDto>> deleteRating(
            @PathVariable @Parameter(description = "Article ID") Integer articleId,
            @PathVariable @Parameter(description = "Rating ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        DeleteRatingResponseDto response = articleRatingService.deleteRating(articleId, id, userId);

        return ResponseEntity.ok(
                ApiResponseDto.success("Calificación eliminada exitosamente", response));
    }

    /**
     * Extract user ID from authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}