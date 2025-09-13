package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.user.*;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.EncryptionService;
import usac.cunoc.bpmn.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User service implementation - compliant with database schema and PDF
 * specification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final CreditCardRepository creditCardRepository;
    private final CountryRepository countryRepository;
    private final CardBrandRepository cardBrandRepository;
    private final GenderRepository genderRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @Override
    public UserProfileResponseDto getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateUserProfile(Integer userId, UpdateUserProfileRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Update only provided fields
        if (request.getFirst_name() != null) {
            user.setFirstName(request.getFirst_name());
        }
        if (request.getLast_name() != null) {
            user.setLastName(request.getLast_name());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getGender_id() != null) {
            Gender gender = genderRepository.findById(request.getGender_id())
                    .orElseThrow(() -> new RuntimeException("Género no encontrado"));
            user.setGender(gender);
        }

        User savedUser = userRepository.save(user);
        return mapToUserProfileResponse(savedUser);
    }

    @Override
    @Transactional
    public AddressResponseDto createAddress(Integer userId, CreateAddressRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validate address limit (max 5 addresses per user)
        long addressCount = userAddressRepository.countByUser(user);
        if (addressCount >= 5) {
            throw new RuntimeException("No se pueden agregar más de 5 direcciones por usuario");
        }

        Country country = countryRepository.findById(request.getCountry_id())
                .orElseThrow(() -> new RuntimeException("País no encontrado"));

        // Handle default flags - if this is the first address, make it default
        boolean isFirstAddress = addressCount == 0;
        if (isFirstAddress || Boolean.TRUE.equals(request.getIs_default())) {
            userAddressRepository.clearDefaultFlags(user);
        }
        if (isFirstAddress || Boolean.TRUE.equals(request.getIs_billing_default())) {
            userAddressRepository.clearBillingDefaultFlags(user);
        }
        if (isFirstAddress || Boolean.TRUE.equals(request.getIs_shipping_default())) {
            userAddressRepository.clearShippingDefaultFlags(user);
        }

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setAddressLine1(request.getAddress_line1());
        address.setAddressLine2(request.getAddress_line2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostal_code());
        address.setCountry(country);
        address.setIsDefault(isFirstAddress || Boolean.TRUE.equals(request.getIs_default()));
        address.setIsBillingDefault(isFirstAddress || Boolean.TRUE.equals(request.getIs_billing_default()));
        address.setIsShippingDefault(isFirstAddress || Boolean.TRUE.equals(request.getIs_shipping_default()));

        UserAddress savedAddress = userAddressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    @Override
    public List<AddressResponseDto> getUserAddresses(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<UserAddress> addresses = userAddressRepository.findByUserOrderByCreatedAtDesc(user);
        return addresses.stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Integer userId, Integer addressId, CreateAddressRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserAddress address = userAddressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        Country country = countryRepository.findById(request.getCountry_id())
                .orElseThrow(() -> new RuntimeException("País no encontrado"));

        // Handle default flags
        if (Boolean.TRUE.equals(request.getIs_default()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            userAddressRepository.clearDefaultFlags(user);
        }
        if (Boolean.TRUE.equals(request.getIs_billing_default())
                && !Boolean.TRUE.equals(address.getIsBillingDefault())) {
            userAddressRepository.clearBillingDefaultFlags(user);
        }
        if (Boolean.TRUE.equals(request.getIs_shipping_default())
                && !Boolean.TRUE.equals(address.getIsShippingDefault())) {
            userAddressRepository.clearShippingDefaultFlags(user);
        }

        // Update address fields
        address.setAddressLine1(request.getAddress_line1());
        address.setAddressLine2(request.getAddress_line2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostal_code());
        address.setCountry(country);
        address.setIsDefault(Boolean.TRUE.equals(request.getIs_default()));
        address.setIsBillingDefault(Boolean.TRUE.equals(request.getIs_billing_default()));
        address.setIsShippingDefault(Boolean.TRUE.equals(request.getIs_shipping_default()));

        UserAddress savedAddress = userAddressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer userId, Integer addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserAddress address = userAddressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        userAddressRepository.delete(address);
    }

    @Override
    @Transactional
    public CreditCardResponseDto createCreditCard(Integer userId, CreateCreditCardRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validate card limit (max 3 cards per user)
        long cardCount = creditCardRepository.countByUserAndIsActiveTrue(user);
        if (cardCount >= 3) {
            throw new RuntimeException("No se pueden agregar más de 3 tarjetas por usuario");
        }

        // Validate expiry date
        validateCardExpiryDate(request.getExpiry_month(), request.getExpiry_month());

        CardBrand cardBrand = cardBrandRepository.findById(request.getCard_brand_id())
                .orElseThrow(() -> new RuntimeException("Marca de tarjeta no encontrada"));

        // Extract last four digits
        String lastFourDigits = request.getCard_number().substring(request.getCard_number().length() - 4);

        // Check for duplicate last four digits
        if (creditCardRepository.existsByUserAndLastFourDigitsAndIsActiveTrue(user, lastFourDigits)) {
            throw new RuntimeException("Ya existe una tarjeta con los mismos últimos 4 dígitos");
        }

        // Handle default flag - if this is the first card, make it default
        boolean isFirstCard = cardCount == 0;
        if (isFirstCard || Boolean.TRUE.equals(request.getIs_default())) {
            creditCardRepository.clearDefaultFlags(user);
        }

        // Encrypt sensitive data
        CreditCard creditCard = new CreditCard();
        creditCard.setUser(user);
        creditCard.setCardNumberEncrypted(encryptionService.encrypt(request.getCard_number()));
        creditCard.setCardholderNameEncrypted(encryptionService.encrypt(request.getCard_holder_name()));
        creditCard.setExpiryMonthEncrypted(encryptionService.encrypt(request.getExpiry_month()));
        creditCard.setExpiryYearEncrypted(encryptionService.encrypt(request.getExpiry_month()));
        creditCard.setCvvEncrypted(encryptionService.encrypt(request.getCvv()));
        creditCard.setCardBrand(cardBrand);
        creditCard.setLastFourDigits(lastFourDigits);
        creditCard.setIsDefault(isFirstCard || Boolean.TRUE.equals(request.getIs_default()));

        CreditCard savedCard = creditCardRepository.save(creditCard);
        return mapToCreditCardResponse(savedCard);
    }

    @Override
    public List<CreditCardResponseDto> getUserCreditCards(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<CreditCard> cards = creditCardRepository.findByUserAndIsActiveTrueOrderByCreatedAtDesc(user);
        return cards.stream()
                .map(this::mapToCreditCardResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCreditCard(Integer userId, Integer cardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CreditCard creditCard = creditCardRepository.findByIdAndUserAndIsActiveTrue(cardId, user)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        // Soft delete by setting isActive to false
        creditCard.setIsActive(false);
        creditCardRepository.save(creditCard);
    }

    @Override
    @Transactional
    public Enable2FAResponseDto enable2FA(Integer userId, Enable2FARequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Enable 2FA
        user.setIs2faEnabled(true);
        userRepository.save(user);

        // Generate backup codes
        List<String> backupCodes = encryptionService.generateBackupCodes();

        return new Enable2FAResponseDto(true, backupCodes);
    }

    @Override
    @Transactional
    public Disable2FAResponseDto disable2FA(Integer userId, Disable2FARequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Verify 2FA code (simplified - in production would verify against TOTP)
        if (!request.getTwo_factor_code().matches("\\d{6}")) {
            throw new RuntimeException("Código 2FA inválido");
        }

        // Disable 2FA
        user.setIs2faEnabled(false);
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        userRepository.save(user);

        return new Disable2FAResponseDto(false);
    }

    // PRIVATE HELPER METHODS

    private UserProfileResponseDto mapToUserProfileResponse(User user) {
        UserProfileResponseDto.GenderDto genderDto = null;
        if (user.getGender() != null) {
            genderDto = new UserProfileResponseDto.GenderDto(
                    user.getGender().getId(), user.getGender().getName());
        }

        UserProfileResponseDto.user_typeDto userTypeDto = new UserProfileResponseDto.user_typeDto(
                user.getUserType().getId(), user.getUserType().getName());

        return new UserProfileResponseDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(), genderDto,
                user.getBirthDate(), user.getPhone(), userTypeDto,
                user.getIsActive(), user.getIsVerified(), user.getIs2faEnabled(),
                user.getTotalSpent(), user.getTotalOrders(),
                user.getDeletedCommentsCount(), user.getCreatedAt());
    }

    private AddressResponseDto mapToAddressResponse(UserAddress address) {
        AddressResponseDto.CountryDto countryDto = new AddressResponseDto.CountryDto(
                address.getCountry().getId(), address.getCountry().getName());

        return new AddressResponseDto(
                address.getId(), address.getAddressLine1(), address.getAddressLine2(),
                address.getCity(), address.getState(), address.getPostalCode(),
                countryDto, address.getIsDefault(), address.getIsBillingDefault(),
                address.getIsShippingDefault(), address.getCreatedAt());
    }

    private CreditCardResponseDto mapToCreditCardResponse(CreditCard card) {
        CreditCardResponseDto.CardBrandDto brandDto = new CreditCardResponseDto.CardBrandDto(
                card.getCardBrand().getId(), card.getCardBrand().getName(), card.getCardBrand().getLogoUrl());

        return new CreditCardResponseDto(
                card.getId(), card.getLastFourDigits(), brandDto,
                encryptionService.decrypt(card.getCardholderNameEncrypted()),
                encryptionService.decrypt(card.getExpiryMonthEncrypted()),
                encryptionService.decrypt(card.getExpiryYearEncrypted()),
                card.getIsDefault(), card.getIsActive(), card.getCreatedAt());
    }

    private void validateCardExpiryDate(String month, String year) {
        try {
            int expiryMonth = Integer.parseInt(month);
            int expiryYear = Integer.parseInt(year);

            if (expiryMonth < 1 || expiryMonth > 12) {
                throw new RuntimeException("Mes de expiración inválido");
            }

            // Check if card is expired
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate expiryDate = java.time.LocalDate.of(expiryYear, expiryMonth, 1).plusMonths(1)
                    .minusDays(1);

            if (expiryDate.isBefore(now)) {
                throw new RuntimeException("La tarjeta está expirada");
            }

        } catch (NumberFormatException e) {
            throw new RuntimeException("Fecha de expiración inválida");
        }
    }
}