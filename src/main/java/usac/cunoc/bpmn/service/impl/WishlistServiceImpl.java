package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import usac.cunoc.bpmn.dto.wishlist.*;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.WishlistService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wishlist service implementation - 100% compliant with database schema and PDF
 * specification
 * Handles all wishlist operations with proper transaction management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final AnalogArticleRepository analogArticleRepository;
    private final UserRepository userRepository;
    private final VinylRepository vinylRepository;
    private final CassetteRepository cassetteRepository;
    private final CdRepository cdRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;

    @Override
    @Transactional(readOnly = true)
    public WishlistResponseDto getWishlist(Integer userId) {
        log.info("Getting wishlist for user: {}", userId);

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Get or create wishlist
        Wishlist wishlist = getOrCreateWishlist(user);

        // Get wishlist items with details
        List<WishlistItem> items = wishlistItemRepository.findByWishlistIdWithDetails(wishlist.getId());

        // Map to DTOs
        List<WishlistResponseDto.WishlistItemDto> itemDtos = items.stream()
                .map(this::mapToWishlistItemDto)
                .collect(Collectors.toList());

        return new WishlistResponseDto(
                wishlist.getId(),
                wishlist.getTotalItems(),
                itemDtos,
                wishlist.getCreatedAt(),
                wishlist.getUpdatedAt());
    }

    @Override
    public AddWishlistItemResponseDto addItemToWishlist(Integer userId, AddWishlistItemRequestDto request) {
        log.info("Adding item {} to wishlist for user: {}", request.getArticle_id(), userId);

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validate article exists
        AnalogArticle article = analogArticleRepository.findById(request.getArticle_id())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        // Get or create wishlist
        Wishlist wishlist = getOrCreateWishlist(user);

        // Check if item already exists in wishlist
        if (wishlistItemRepository.existsByUserIdAndArticleId(userId, request.getArticle_id())) {
            throw new RuntimeException("El artículo ya está en la lista de deseos");
        }

        // Create new wishlist item
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setAnalogArticle(article);
        wishlistItem.setIsPreorderPaid(false);
        wishlistItem.setNotificationSent(false);
        wishlistItem.setCreatedAt(LocalDateTime.now());

        WishlistItem savedItem = wishlistItemRepository.save(wishlistItem);

        // Update wishlist total items
        updateWishlistTotalItems(wishlist);

        log.info("Added item {} to wishlist for user: {}", savedItem.getId(), userId);

        return new AddWishlistItemResponseDto(
                wishlist.getId(),
                savedItem.getId(),
                article.getId(),
                savedItem.getCreatedAt());
    }

    @Override
    public RemoveWishlistItemResponseDto removeItemFromWishlist(Integer itemId, Integer userId) {
        log.info("Removing wishlist item {} for user: {}", itemId, userId);

        // Find item and validate user ownership
        WishlistItem wishlistItem = wishlistItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new RuntimeException("Elemento de lista de deseos no encontrado"));

        Wishlist wishlist = wishlistItem.getWishlist();
        Integer wishlistId = wishlist.getId();

        // Remove item
        wishlistItemRepository.delete(wishlistItem);

        // Update wishlist total items
        updateWishlistTotalItems(wishlist);

        // Get updated total items
        Integer newTotalItems = wishlistRepository.findById(wishlistId)
                .map(Wishlist::getTotalItems)
                .orElse(0);

        log.info("Removed wishlist item {} for user: {}", itemId, userId);

        return new RemoveWishlistItemResponseDto(
                itemId,
                wishlistId,
                newTotalItems);
    }

    @Override
    public WishlistPreorderPaymentResponseDto processPreorderPayment(Integer itemId,
            WishlistPreorderPaymentRequestDto request,
            Integer userId) {
        log.info("Processing preorder payment for wishlist item {} by user: {}", itemId, userId);

        // Find item and validate user ownership
        WishlistItem wishlistItem = wishlistItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new RuntimeException("Elemento de lista de deseos no encontrado"));

        AnalogArticle article = wishlistItem.getAnalogArticle();

        // Validate article is preorder
        if (!article.getIsPreorder()) {
            throw new RuntimeException("El artículo no es una preorden");
        }

        // Check if already paid
        if (wishlistItem.getIsPreorderPaid()) {
            throw new RuntimeException("La preorden ya ha sido pagada");
        }

        // Validate payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPayment_method_id())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // Get pending payment status
        PaymentStatus pendingStatus = paymentStatusRepository.findByName("Pendiente")
                .orElseThrow(() -> new RuntimeException("Estado de pago 'Pendiente' no encontrado"));

        // Create preorder payment
        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(pendingStatus);
        payment.setCurrency(article.getCurrency());
        payment.setAmount(article.getPrice());
        payment.setProcessedAt(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Mark wishlist item as paid
        wishlistItem.setIsPreorderPaid(true);
        wishlistItemRepository.save(wishlistItem);

        log.info("Processed preorder payment {} for wishlist item {}", savedPayment.getId(), itemId);

        return new WishlistPreorderPaymentResponseDto(
                savedPayment.getId(),
                wishlistItem.getId(),
                article.getId(),
                article.getPrice(),
                true,
                savedPayment.getProcessedAt());
    }

    /**
     * Get existing wishlist or create new one for user
     */
    private Wishlist getOrCreateWishlist(User user) {
        return wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUser(user);
                    newWishlist.setTotalItems(0);
                    newWishlist.setCreatedAt(LocalDateTime.now());
                    newWishlist.setUpdatedAt(LocalDateTime.now());
                    return wishlistRepository.save(newWishlist);
                });
    }

    /**
     * Update wishlist total items count
     */
    private void updateWishlistTotalItems(Wishlist wishlist) {
        List<WishlistItem> items = wishlistItemRepository.findByWishlistIdWithDetails(wishlist.getId());
        wishlist.setTotalItems(items.size());
        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);
    }

    /**
     * Map WishlistItem entity to DTO with all required details
     */
    private WishlistResponseDto.WishlistItemDto mapToWishlistItemDto(WishlistItem item) {
        AnalogArticle article = item.getAnalogArticle();

        // Create article DTO
        WishlistResponseDto.ArticleDto articleDto = new WishlistResponseDto.ArticleDto(
                article.getId(),
                article.getTitle(),
                new WishlistResponseDto.ArtistDto(article.getArtist().getId(), article.getArtist().getName()),
                determineArticleType(article),
                article.getPrice(),
                new CurrencyDto(article.getCurrency().getCode(), article.getCurrency().getSymbol()),
                article.getImageUrl(),
                article.getIsAvailable(),
                article.getIsPreorder(),
                article.getStockQuantity());

        return new WishlistResponseDto.WishlistItemDto(
                item.getId(),
                articleDto,
                item.getIsPreorderPaid(),
                item.getNotificationSent(),
                item.getCreatedAt());
    }

    /**
     * Determine article type by checking related tables
     */
    private String determineArticleType(AnalogArticle article) {
        if (vinylRepository.existsByAnalogArticleId(article.getId())) {
            return "vinyl";
        } else if (cassetteRepository.existsByAnalogArticleId(article.getId())) {
            return "cassette";
        } else if (cdRepository.existsByAnalogArticleId(article.getId())) {
            return "cd";
        }
        return "unknown";
    }

    /**
     * Generate unique payment number
     */
    private String generatePaymentNumber() {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PREORDER-" + timestamp + "-" + randomPart;
    }
}