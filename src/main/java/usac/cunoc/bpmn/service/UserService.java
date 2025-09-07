package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.user.*;
import java.util.List;

/**
 * User service interface for user profile and related operations
 */
public interface UserService {

    /**
     * Get user profile
     */
    UserProfileResponseDto getUserProfile(Integer userId);

    /**
     * Update user profile
     */
    UserProfileResponseDto updateUserProfile(Integer userId, UpdateUserProfileRequestDto request);

    /**
     * Create user address
     */
    AddressResponseDto createAddress(Integer userId, CreateAddressRequestDto request);

    /**
     * Get user addresses
     */
    List<AddressResponseDto> getUserAddresses(Integer userId);

    /**
     * Update user address
     */
    AddressResponseDto updateAddress(Integer userId, Integer addressId, CreateAddressRequestDto request);

    /**
     * Delete user address
     */
    void deleteAddress(Integer userId, Integer addressId);

    /**
     * Create credit card
     */
    CreditCardResponseDto createCreditCard(Integer userId, CreateCreditCardRequestDto request);

    /**
     * Get user credit cards
     */
    List<CreditCardResponseDto> getUserCreditCards(Integer userId);

    /**
     * Delete credit card
     */
    void deleteCreditCard(Integer userId, Integer cardId);

    /**
     * Enable 2FA
     */
    Enable2FAResponseDto enable2FA(Integer userId, Enable2FARequestDto request);

    /**
     * Disable 2FA
     */
    Disable2FAResponseDto disable2FA(Integer userId, Disable2FARequestDto request);
}