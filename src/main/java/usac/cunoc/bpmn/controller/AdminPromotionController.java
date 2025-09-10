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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.promotion.CreatePromotionRequestDto;
import usac.cunoc.bpmn.dto.promotion.CreatePromotionResponseDto;
import usac.cunoc.bpmn.dto.promotion.UpdatePromotionRequestDto;
import usac.cunoc.bpmn.dto.promotion.UpdatePromotionResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminPromotionService;

/**
 * Admin promotion controller for administrative CD promotion operations -
 * matches PDF specification exactly
 */
@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Admin Promotions", description = "Administrative CD promotion operations")
public class AdminPromotionController {

    private final AdminPromotionService adminPromotionService;
    private final UserRepository userRepository;

    @PostMapping("/cd")
    @Operation(summary = "Create CD promotion", description = "Create a new CD promotion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CD promotion created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<CreatePromotionResponseDto>> createCdPromotion(
            @Valid @RequestBody CreatePromotionRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        CreatePromotionResponseDto response = adminPromotionService.createCdPromotion(request, adminUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Promoción creada exitosamente", response));
    }

    @PutMapping("/cd/{id}")
    @Operation(summary = "Update CD promotion", description = "Update an existing CD promotion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CD promotion updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Promotion not found")
    })
    public ResponseEntity<ApiResponseDto<UpdatePromotionResponseDto>> updateCdPromotion(
            @PathVariable @Parameter(description = "Promotion ID") Integer id,
            @Valid @RequestBody UpdatePromotionRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        UpdatePromotionResponseDto response = adminPromotionService.updateCdPromotion(id, request, adminUserId);

        return ResponseEntity.ok(ApiResponseDto.success("Promoción actualizada exitosamente", response));
    }

    @DeleteMapping("/cd/{id}")
    @Operation(summary = "Delete CD promotion", description = "Delete an existing CD promotion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CD promotion deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Promotion not found")
    })
    public ResponseEntity<ApiResponseDto<Void>> deleteCdPromotion(
            @PathVariable @Parameter(description = "Promotion ID") Integer id,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        adminPromotionService.deleteCdPromotion(id, adminUserId);

        return ResponseEntity.ok(ApiResponseDto.success("Promoción eliminada exitosamente"));
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