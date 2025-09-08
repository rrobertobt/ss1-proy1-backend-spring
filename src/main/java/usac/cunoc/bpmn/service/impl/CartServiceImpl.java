package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.cart.*;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.CartService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cart service implementation - 100% compliant with database schema and PDF
 * structure
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final AnalogArticleRepository analogArticleRepository;
    private final CdPromotionRepository cdPromotionRepository;
    private final UserRepository userRepository;
    private final VinylRepository vinylRepository;
    private final CassetteRepository cassetteRepository;
    private final CdRepository cdRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Integer userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        List<ShoppingCartItem> items = shoppingCartItemRepository.findByCartIdWithDetails(cart.getId());

        List<CartResponseDto.CartItemDto> itemDtos = items.stream()
                .map(this::mapToCartItemDto)
                .collect(Collectors.toList());

        return new CartResponseDto(
                cart.getId(),
                cart.getTotalItems(),
                cart.getSubtotal(),
                itemDtos);
    }

    @Override
    public AddCartItemResponseDto addItemToCart(Integer userId, AddCartItemRequestDto request) {
        // Validate article exists and has sufficient stock
        AnalogArticle article = analogArticleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        if (!article.getIsAvailable()) {
            throw new RuntimeException("Artículo no disponible");
        }

        if (article.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + article.getStockQuantity());
        }

        // Get or create cart
        ShoppingCart cart = getOrCreateCart(userId);

        // Check if item already exists in cart
        ShoppingCartItem existingItem = shoppingCartItemRepository
                .findByCartIdAndArticleId(cart.getId(), request.getArticleId())
                .orElse(null);

        ShoppingCartItem cartItem;
        if (existingItem != null) {
            // Update existing item quantity
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (article.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Stock insuficiente para cantidad total: " + newQuantity);
            }
            existingItem.setQuantity(newQuantity);
            cartItem = shoppingCartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            cartItem = new ShoppingCartItem();
            cartItem.setShoppingCart(cart);
            cartItem.setAnalogArticle(article);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(article.getPrice());
            cartItem.setDiscountApplied(BigDecimal.ZERO);
            cartItem = shoppingCartItemRepository.save(cartItem);
        }

        // Refresh cart to get updated totals (triggers handle this automatically)
        cart = shoppingCartRepository.findById(cart.getId()).orElseThrow();

        log.info("Added item {} to cart {} for user {}", request.getArticleId(), cart.getId(), userId);

        return new AddCartItemResponseDto(
                cart.getId(),
                cartItem.getId(),
                article.getId(),
                cartItem.getQuantity(),
                cartItem.getUnitPrice(),
                cartItem.getTotalPrice(),
                cart.getSubtotal());
    }

    @Override
    public UpdateCartItemResponseDto updateCartItemQuantity(Integer userId, Integer itemId,
            UpdateCartItemRequestDto request) {
        ShoppingCart cart = getOrCreateCart(userId);

        ShoppingCartItem cartItem = shoppingCartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));

        // Verify item belongs to user's cart
        if (!cartItem.getShoppingCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item no pertenece al carrito del usuario");
        }

        // Validate stock
        if (cartItem.getAnalogArticle().getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " +
                    cartItem.getAnalogArticle().getStockQuantity());
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem = shoppingCartItemRepository.save(cartItem);

        // Refresh cart to get updated totals
        cart = shoppingCartRepository.findById(cart.getId()).orElseThrow();

        log.info("Updated cart item {} quantity to {} for user {}", itemId, request.getQuantity(), userId);

        return new UpdateCartItemResponseDto(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getUnitPrice(),
                cartItem.getTotalPrice(),
                cart.getSubtotal());
    }

    @Override
    public RemoveCartItemResponseDto removeItemFromCart(Integer userId, Integer itemId) {
        ShoppingCart cart = getOrCreateCart(userId);

        ShoppingCartItem cartItem = shoppingCartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));

        // Verify item belongs to user's cart
        if (!cartItem.getShoppingCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item no pertenece al carrito del usuario");
        }

        shoppingCartItemRepository.delete(cartItem);

        // Refresh cart to get updated totals
        cart = shoppingCartRepository.findById(cart.getId()).orElseThrow();

        log.info("Removed cart item {} for user {}", itemId, userId);

        return new RemoveCartItemResponseDto(
                itemId,
                cart.getSubtotal(),
                cart.getTotalItems());
    }

    @Override
    public void clearCart(Integer userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        shoppingCartItemRepository.deleteByShoppingCartId(cart.getId());

        log.info("Cleared cart for user {}", userId);
    }

    @Override
    public ApplyPromotionResponseDto applyCdPromotion(Integer userId, ApplyPromotionRequestDto request) {
        ShoppingCart cart = getOrCreateCart(userId);

        CdPromotion promotion = cdPromotionRepository.findActivePromotionById(
                request.getPromotionId(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada o no activa"));

        // Validate promotion limits
        if (request.getArticleIds().size() > promotion.getMaxItems()) {
            throw new RuntimeException("Promoción permite máximo " + promotion.getMaxItems() + " artículos");
        }

        List<ShoppingCartItem> itemsToUpdate = new ArrayList<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Apply promotion to specified items
        for (Integer articleId : request.getArticleIds()) {
            ShoppingCartItem cartItem = shoppingCartItemRepository
                    .findByCartIdAndArticleId(cart.getId(), articleId)
                    .orElseThrow(() -> new RuntimeException("Artículo " + articleId + " no está en el carrito"));

            // Validate article is CD if promotion is genre-based
            if (promotion.getMusicGenre() != null) {
                validateCdForGenrePromotion(cartItem.getAnalogArticle(), promotion.getMusicGenre().getId());
            } else {
                validateIsCd(cartItem.getAnalogArticle());
            }

            // Calculate discount amount
            BigDecimal itemTotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            BigDecimal discount = itemTotal.multiply(promotion.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            cartItem.setDiscountApplied(discount);
            cartItem.setCdPromotion(promotion);
            itemsToUpdate.add(cartItem);
            totalDiscount = totalDiscount.add(discount);
        }

        // Save all updated items
        shoppingCartItemRepository.saveAll(itemsToUpdate);

        // Refresh cart to get updated totals
        cart = shoppingCartRepository.findById(cart.getId()).orElseThrow();

        List<Integer> updatedItemIds = itemsToUpdate.stream()
                .map(ShoppingCartItem::getId)
                .collect(Collectors.toList());

        log.info("Applied promotion {} to {} items for user {}",
                request.getPromotionId(), updatedItemIds.size(), userId);

        return new ApplyPromotionResponseDto(
                promotion.getId(),
                updatedItemIds,
                totalDiscount,
                cart.getSubtotal());
    }

    // Helper methods
    private ShoppingCart getOrCreateCart(Integer userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));
    }

    private ShoppingCart createCartForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        cart.setTotalItems(0);
        cart.setSubtotal(BigDecimal.ZERO);
        return shoppingCartRepository.save(cart);
    }

    private CartResponseDto.CartItemDto mapToCartItemDto(ShoppingCartItem item) {
        CartResponseDto.ArticleDto articleDto = new CartResponseDto.ArticleDto(
                item.getAnalogArticle().getId(),
                item.getAnalogArticle().getTitle(),
                item.getAnalogArticle().getArtist().getName(),
                item.getAnalogArticle().getImageUrl(),
                determineArticleType(item.getAnalogArticle().getId()));

        CartResponseDto.PromotionDto promotionDto = null;
        if (item.getCdPromotion() != null) {
            promotionDto = new CartResponseDto.PromotionDto(
                    item.getCdPromotion().getId(),
                    item.getCdPromotion().getName(),
                    item.getCdPromotion().getDiscountPercentage());
        }

        return new CartResponseDto.CartItemDto(
                item.getId(),
                articleDto,
                item.getQuantity(),
                item.getUnitPrice(),
                item.getDiscountApplied(),
                item.getTotalPrice(),
                promotionDto);
    }

    private String determineArticleType(Integer articleId) {
        if (vinylRepository.existsByAnalogArticleId(articleId)) {
            return "vinyl";
        } else if (cassetteRepository.existsByAnalogArticleId(articleId)) {
            return "cassette";
        } else if (cdRepository.existsByAnalogArticleId(articleId)) {
            return "cd";
        }
        return "unknown";
    }

    private void validateIsCd(AnalogArticle article) {
        if (!cdRepository.existsByAnalogArticleId(article.getId())) {
            throw new RuntimeException("La promoción solo aplica a CDs");
        }
    }

    private void validateCdForGenrePromotion(AnalogArticle article, Integer genreId) {
        validateIsCd(article);
        if (!article.getMusicGenre().getId().equals(genreId)) {
            throw new RuntimeException("El CD no pertenece al género de la promoción");
        }
    }
}