package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioAccessResponseDto;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioDownloadResponseDto;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioPlayResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.PreorderAudioService;

/**
 * Preorder audio controller for user audio access operations - matches PDF
 * specification exactly
 * Handles all preorder audio endpoints: access list, play tracking, and
 * downloads
 */
@RestController
@RequestMapping("/api/v1/preorder-audios")
@RequiredArgsConstructor
@Tag(name = "Preorder Audios", description = "Preorder audio access and management operations")
public class PreorderAudioController {

    private final PreorderAudioService preorderAudioService;
    private final UserRepository userRepository;

    @GetMapping("/my-access")
    @Operation(summary = "Get user's accessible preorder audios", description = "Retrieve all preorder audios the current user has access to")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accessible audios retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponseDto<PreorderAudioAccessResponseDto>> getMyAccessibleAudios(
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        PreorderAudioAccessResponseDto response = preorderAudioService.getUserAccessibleAudios(userId);

        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @PostMapping("/{id}/play")
    @Operation(summary = "Register audio play", description = "Track and register that user played a specific preorder audio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audio play registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid audio ID or user doesn't have access"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Audio or user not found")
    })
    public ResponseEntity<ApiResponseDto<PreorderAudioPlayResponseDto>> registerAudioPlay(
            @PathVariable @Parameter(description = "Preorder audio ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        PreorderAudioPlayResponseDto response = preorderAudioService.registerAudioPlay(id, userId);

        return ResponseEntity.ok(
                ApiResponseDto.success("Reproducción registrada exitosamente", response));
    }

    @PostMapping("/{id}/download")
    @Operation(summary = "Generate download link", description = "Generate temporary download link for preorder audio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Download link generated successfully"),
            @ApiResponse(responseCode = "400", description = "Audio not downloadable or user doesn't have access"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Audio or user not found")
    })
    public ResponseEntity<ApiResponseDto<PreorderAudioDownloadResponseDto>> generateDownloadLink(
            @PathVariable @Parameter(description = "Preorder audio ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        PreorderAudioDownloadResponseDto response = preorderAudioService.generateDownloadLink(id, userId);

        return ResponseEntity.ok(
                ApiResponseDto.success("Descarga iniciada exitosamente", response));
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