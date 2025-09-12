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
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.admin.catalog.master.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminCatalogMasterService;

/**
 * Admin catalog master controller for administrative catalog master operations
 * - matches PDF
 * specification exactly. Requires ADMIN role for all operations.
 */
@RestController
@RequestMapping("/api/v1/admin/catalogs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Admin Catalog Master Management", description = "Administrative catalog master data operations")
public class AdminCatalogMasterController {

    private final AdminCatalogMasterService adminCatalogMasterService;
    private final UserRepository userRepository;

    @PostMapping("/artists")
    @Operation(summary = "Create new artist", description = "Create a new artist in the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artist created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<CreateArtistResponseDto>> createArtist(
            @Valid @RequestBody CreateArtistRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        CreateArtistResponseDto response = adminCatalogMasterService.createArtist(request, adminUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Artista creado exitosamente", response));
    }

    @PutMapping("/artists/{id}")
    @Operation(summary = "Update artist", description = "Update an existing artist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Artist not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<UpdateArtistResponseDto>> updateArtist(
            @Parameter(description = "Artist ID") @PathVariable Integer id,
            @Valid @RequestBody UpdateArtistRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        UpdateArtistResponseDto response = adminCatalogMasterService.updateArtist(id, request, adminUserId);

        return ResponseEntity.ok(ApiResponseDto.success("Artista actualizado exitosamente", response));
    }

    @DeleteMapping("/artists/{id}")
    @Operation(summary = "Delete artist", description = "Soft delete an artist (mark as inactive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Artist not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<DeleteArtistResponseDto>> deleteArtist(
            @Parameter(description = "Artist ID") @PathVariable Integer id,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        DeleteArtistResponseDto response = adminCatalogMasterService.deleteArtist(id, adminUserId);

        return ResponseEntity.ok(ApiResponseDto.success("Artista eliminado exitosamente", response));
    }

    @PostMapping("/genres")
    @Operation(summary = "Create new music genre", description = "Create a new music genre in the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<CreateGenreResponseDto>> createGenre(
            @Valid @RequestBody CreateGenreRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        CreateGenreResponseDto response = adminCatalogMasterService.createGenre(request, adminUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Género musical creado exitosamente", response));
    }

    @PostMapping("/vinyl-special-editions")
    @Operation(summary = "Create vinyl special edition", description = "Create a new vinyl special edition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Special edition created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<CreateVinylSpecialEditionResponseDto>> createVinylSpecialEdition(
            @Valid @RequestBody CreateVinylSpecialEditionRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        CreateVinylSpecialEditionResponseDto response = adminCatalogMasterService.createVinylSpecialEdition(request,
                adminUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Edición especial de vinilo creada exitosamente", response));
    }

    /**
     * Get current user ID from authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        String usernameOrEmail = authentication.getName();
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
