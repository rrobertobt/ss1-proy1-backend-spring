package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.promotion.PromotionListResponseDto;
import usac.cunoc.bpmn.service.PromotionService;

/**
 * Promotion controller for public CD promotion operations - matches PDF
 * specification exactly
 */
@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Public CD promotion operations")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping("/cd")
    @Operation(summary = "Get CD promotions", description = "Get available CD promotions with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CD promotions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    })
    public ResponseEntity<ApiResponseDto<PromotionListResponseDto>> getCdPromotions(
            @RequestParam(required = false) @Parameter(description = "Filter by promotion type") String type,
            @RequestParam(required = false) @Parameter(description = "Filter by genre ID") Integer genreId,
            @RequestParam(required = false) @Parameter(description = "Filter by active status") Boolean isActive) {

        PromotionListResponseDto promotions = promotionService.getCdPromotions(type, genreId, isActive);
        return ResponseEntity.ok(ApiResponseDto.success(promotions));
    }
}