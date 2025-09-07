package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import usac.cunoc.bpmn.dto.user.*;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.UserService;
import java.util.List;

/**
 * User controller with responses matching PDF specification exactly
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User profile and account management operations")
public class UserController {

        private final UserService userService;
        private final UserRepository userRepository;

        @GetMapping("/profile")
        @Operation(summary = "Get user profile", description = "Get current user profile information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ApiResponseDto<UserProfileResponseDto>> getUserProfile(Authentication authentication) {
                Integer userId = getCurrentUserId(authentication);
                UserProfileResponseDto profile = userService.getUserProfile(userId);
                return ResponseEntity.ok(ApiResponseDto.success(profile));
        }

        @PutMapping("/profile")
        @Operation(summary = "Update user profile", description = "Update current user profile information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponseDto<UserProfileResponseDto>> updateUserProfile(
                        @Valid @RequestBody UpdateUserProfileRequestDto request,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                UserProfileResponseDto updatedProfile = userService.updateUserProfile(userId, request);
                return ResponseEntity.ok(ApiResponseDto.success("Perfil actualizado exitosamente", updatedProfile));
        }

        @PostMapping("/addresses")
        @Operation(summary = "Add user address", description = "Add new address for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Address created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponseDto<AddressResponseDto>> createAddress(
                        @Valid @RequestBody CreateAddressRequestDto request,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                AddressResponseDto address = userService.createAddress(userId, request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseDto.success("Dirección agregada exitosamente", address));
        }

        @GetMapping("/addresses")
        @Operation(summary = "Get user addresses", description = "Get all addresses for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponseDto<UserAddressListResponseDto>> getUserAddresses(
                        Authentication authentication) {
                Integer userId = getCurrentUserId(authentication);
                List<AddressResponseDto> addresses = userService.getUserAddresses(userId);

                UserAddressListResponseDto response = new UserAddressListResponseDto(addresses);
                return ResponseEntity.ok(ApiResponseDto.success(response));
        }

        @PutMapping("/addresses/{id}")
        @Operation(summary = "Update user address", description = "Update existing address for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        public ResponseEntity<ApiResponseDto<AddressResponseDto>> updateAddress(
                        @PathVariable Integer id,
                        @Valid @RequestBody CreateAddressRequestDto request,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                AddressResponseDto address = userService.updateAddress(userId, id, request);
                return ResponseEntity.ok(ApiResponseDto.success("Dirección actualizada exitosamente", address));
        }

        @DeleteMapping("/addresses/{id}")
        @Operation(summary = "Delete user address", description = "Delete address for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        public ResponseEntity<ApiResponseDto<Void>> deleteAddress(
                        @PathVariable Integer id,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                userService.deleteAddress(userId, id);
                return ResponseEntity.ok(ApiResponseDto.success("Dirección eliminada exitosamente"));
        }

        @PostMapping("/cards")
        @Operation(summary = "Add credit card", description = "Add new credit card for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Card added successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponseDto<CreditCardResponseDto>> createCreditCard(
                        @Valid @RequestBody CreateCreditCardRequestDto request,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                CreditCardResponseDto card = userService.createCreditCard(userId, request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseDto.success("Tarjeta agregada exitosamente", card));
        }

        @GetMapping("/cards")
        @Operation(summary = "Get user credit cards", description = "Get all credit cards for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponseDto<CreditCardListResponseDto>> getUserCreditCards(
                        Authentication authentication) {
                Integer userId = getCurrentUserId(authentication);
                List<CreditCardResponseDto> cards = userService.getUserCreditCards(userId);

                CreditCardListResponseDto response = new CreditCardListResponseDto(cards);
                return ResponseEntity.ok(ApiResponseDto.success(response));
        }

        @DeleteMapping("/cards/{id}")
        @Operation(summary = "Delete credit card", description = "Delete credit card for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Card deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Card not found")
        })
        public ResponseEntity<ApiResponseDto<Void>> deleteCreditCard(
                        @PathVariable Integer id,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                userService.deleteCreditCard(userId, id);
                return ResponseEntity.ok(ApiResponseDto.success("Tarjeta eliminada exitosamente"));
        }

        @PostMapping("/enable-2fa")
        @Operation(summary = "Enable 2FA", description = "Enable two-factor authentication for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "2FA enabled successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid password"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponseDto<Enable2FAResponseDto>> enable2FA(
                        @Valid @RequestBody Enable2FARequestDto request,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                Enable2FAResponseDto response = userService.enable2FA(userId, request);
                return ResponseEntity.ok(ApiResponseDto.success("Autenticación de dos factores habilitada", response));
        }

        @PostMapping("/disable-2fa")
        @Operation(summary = "Disable 2FA", description = "Disable two-factor authentication for current user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "2FA disabled successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid password or 2FA code"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponseDto<Disable2FAResponseDto>> disable2FA(
                        @Valid @RequestBody Disable2FARequestDto request,
                        Authentication authentication) {

                Integer userId = getCurrentUserId(authentication);
                Disable2FAResponseDto response = userService.disable2FA(userId, request);
                return ResponseEntity
                                .ok(ApiResponseDto.success("Autenticación de dos factores deshabilitada", response));
        }

        // PRIVATE HELPER METHODS

        private Integer getCurrentUserId(Authentication authentication) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return userRepository.findByUsernameOrEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                                .getId();
        }

        // Wrapper classes to match PDF JSON structure exactly
        public record AddressListWrapper(List<AddressResponseDto> addresses) {
        }

        public record CreditCardListWrapper(List<CreditCardResponseDto> cards) {
        }
}