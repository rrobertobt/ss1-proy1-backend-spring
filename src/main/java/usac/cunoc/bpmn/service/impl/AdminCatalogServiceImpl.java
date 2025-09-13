package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.admin.catalog.*;
import usac.cunoc.bpmn.dto.catalog.ArticleDetailResponseDto;
import usac.cunoc.bpmn.dto.catalog.CatalogArticlesResponseDto;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.AnalogArticleRepository;
import usac.cunoc.bpmn.repository.ArtistRepository;
import usac.cunoc.bpmn.repository.MusicGenreRepository;
import usac.cunoc.bpmn.repository.CurrencyRepository;
import usac.cunoc.bpmn.repository.VinylRepository;
import usac.cunoc.bpmn.repository.VinylCategoryRepository;
import usac.cunoc.bpmn.repository.VinylSpecialEditionRepository;
import usac.cunoc.bpmn.repository.CassetteRepository;
import usac.cunoc.bpmn.repository.CassetteCategoryRepository;
import usac.cunoc.bpmn.repository.CdRepository;
import usac.cunoc.bpmn.repository.StockMovementRepository;
import usac.cunoc.bpmn.repository.MovementTypeRepository;
import usac.cunoc.bpmn.repository.MovementReferenceTypeRepository;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminCatalogService;
import usac.cunoc.bpmn.service.CatalogService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin catalog service implementation - 100% compliant with database schema
 * and PDF specification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCatalogServiceImpl implements AdminCatalogService {

    private final AnalogArticleRepository analogArticleRepository;
    private final ArtistRepository artistRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final CurrencyRepository currencyRepository;
    private final VinylRepository vinylRepository;
    private final VinylCategoryRepository vinylCategoryRepository;
    private final VinylSpecialEditionRepository vinylSpecialEditionRepository;
    private final CassetteRepository cassetteRepository;
    private final CassetteCategoryRepository cassetteCategoryRepository;
    private final CdRepository cdRepository;
    private final StockMovementRepository stockMovementRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final MovementReferenceTypeRepository movementReferenceTypeRepository;
    private final UserRepository userRepository;
    private final CatalogService catalogService;

    @Override
    @Transactional
    public CreateArticleResponseDto createArticle(CreateArticleRequestDto request, Integer adminUserId) {
        // Validate required entities exist
        Artist artist = artistRepository.findById(request.getArtist_id())
                .orElseThrow(() -> new RuntimeException("Artista no encontrado"));

        MusicGenre genre = musicGenreRepository.findById(request.getMusic_genre_id())
                .orElseThrow(() -> new RuntimeException("Género musical no encontrado"));

        Currency currency = currencyRepository.findById(request.getCurrency_id())
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));

        // Create main article
        AnalogArticle article = new AnalogArticle();
        article.setTitle(request.getTitle());
        article.setArtist(artist);
        article.setPrice(request.getPrice());
        article.setCurrency(currency);
        article.setMusicGenre(genre);
        article.setReleaseDate(request.getRelease_date());
        article.setDescription(request.getDescription());
        article.setDimensions(request.getDimensions());
        article.setWeightGrams(request.getWeight_grams());
        article.setBarcode(request.getBarcode());
        article.setStockQuantity(request.getStock_quantity());
        article.setMinStockLevel(request.getMin_stock_level());
        article.setMaxStockLevel(request.getMax_stock_level());
        article.setIsAvailable(request.getIs_available());
        article.setIsPreorder(request.getIs_preorder());
        article.setPreorderReleaseDate(request.getPre_order_release_date());
        article.setPreorderEndDate(request.getPre_order_release_date());
        article.setImageUrl(request.getImage_url());

        // Save main article
        AnalogArticle savedArticle = analogArticleRepository.save(article);
        log.info("Created analog article with ID: {}", savedArticle.getId());

        // Create type-specific record
        createTypeSpecificRecord(savedArticle, request.getType(), request.getType_details());

        // Create initial stock movement if stock > 0
        if (request.getStock_quantity() > 0) {
            createInitialStockMovement(savedArticle, request.getStock_quantity(), adminUserId);
        }

        return new CreateArticleResponseDto(
                savedArticle.getId(),
                savedArticle.getTitle(),
                request.getType(),
                savedArticle.getCreatedAt());
    }

    @Override
    public ArticleDetailResponseDto getArticleById(Integer articleId) {
        return catalogService.getArticleById(articleId);
    }

    @Override
    @Transactional
    public ArticleDetailResponseDto updateArticle(Integer articleId, CreateArticleRequestDto request,
            Integer adminUserId) {
        AnalogArticle article = analogArticleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        // Validate required entities
        Artist artist = artistRepository.findById(request.getArtist_id())
                .orElseThrow(() -> new RuntimeException("Artista no encontrado"));

        MusicGenre genre = musicGenreRepository.findById(request.getMusic_genre_id())
                .orElseThrow(() -> new RuntimeException("Género musical no encontrado"));

        Currency currency = currencyRepository.findById(request.getCurrency_id())
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));

        // Update fields
        article.setTitle(request.getTitle());
        article.setArtist(artist);
        article.setPrice(request.getPrice());
        article.setCurrency(currency);
        article.setMusicGenre(genre);
        article.setReleaseDate(request.getRelease_date());
        article.setDescription(request.getDescription());
        article.setDimensions(request.getDimensions());
        article.setWeightGrams(request.getWeight_grams());
        article.setBarcode(request.getBarcode());
        article.setMinStockLevel(request.getMin_stock_level());
        article.setMaxStockLevel(request.getMax_stock_level());
        article.setIsAvailable(request.getIs_available());
        article.setIsPreorder(request.getIs_preorder());
        article.setPreorderReleaseDate(request.getPre_order_release_date());
        article.setPreorderEndDate(request.getPre_order_release_date());
        article.setImageUrl(request.getImage_url());

        analogArticleRepository.save(article);
        log.info("Updated article with ID: {}", articleId);

        return getArticleById(articleId);
    }

    @Override
    @Transactional
    public void deleteArticle(Integer articleId, Integer adminUserId) {
        AnalogArticle article = analogArticleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        // Soft delete - mark as unavailable
        article.setIsAvailable(false);
        analogArticleRepository.save(article);

        log.info("Soft deleted article with ID: {} by admin user: {}", articleId, adminUserId);
    }

    @Override
    public CatalogArticlesResponseDto getArticlesForAdmin(Integer page, Integer limit, String search,
            Integer genreId, Integer artistId, String type, Boolean isAvailable) {

        // Use catalog service but without availability filter for admin
        return catalogService.getArticles(page, limit, search, genreId, artistId,
                null, null, type, null, isAvailable);
    }

    @Override
    @Transactional
    public StockMovementResponseDto createStockMovement(Integer articleId,
            StockMovementRequestDto request, Integer adminUserId) {

        // Validate article exists
        AnalogArticle article = analogArticleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        // Validate admin user exists
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));

        // Get movement type (Entrada/Salida)
        MovementType movementType = movementTypeRepository.findByName(request.getMovement_type())
                .orElseThrow(
                        () -> new RuntimeException("Tipo de movimiento no encontrado: " + request.getMovement_type()));

        // Get movement reference type
        MovementReferenceType referenceType = movementReferenceTypeRepository.findByName(request.getReference_type())
                .orElseThrow(
                        () -> new RuntimeException("Tipo de referencia no encontrado: " + request.getReference_type()));

        // Create stock movement (triggers will update stock automatically)
        StockMovement movement = new StockMovement();
        movement.setAnalogArticle(article);
        movement.setMovementType(movementType);
        movement.setMovementReferenceType(referenceType);
        movement.setQuantity(request.getQuantity());
        movement.setReferenceId(request.getReference_id());
        movement.setNotes(request.getNotes());
        movement.setCreatedByUser(adminUser);

        // The trigger will set previousStock and newStock automatically
        StockMovement savedMovement = stockMovementRepository.save(movement);
        log.info("Created stock movement with ID: {} for article: {}", savedMovement.getId(), articleId);

        return new StockMovementResponseDto(
                savedMovement.getId(),
                articleId,
                savedMovement.getMovementType().getName(),
                savedMovement.getQuantity(),
                savedMovement.getPreviousStock(),
                savedMovement.getNewStock(),
                savedMovement.getCreatedAt());
    }

    @Override
    public StockMovementListResponseDto getStockMovements(Integer page, Integer limit,
            Integer articleId, String movementType, LocalDate dateFrom, LocalDate dateTo) {

        // Default pagination
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = limit != null && limit > 0 ? limit : 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // Get movements with filters
        Page<StockMovement> movementPage = stockMovementRepository.findMovementsWithFilters(
                articleId, movementType, dateFrom, dateTo, pageable);

        // Map to DTOs
        List<StockMovementListResponseDto.StockMovementItemDto> movements = movementPage.getContent()
                .stream()
                .map(this::mapToMovementItemDto)
                .collect(Collectors.toList());

        // Build pagination
        PaginationDto pagination = new PaginationDto(
                pageNumber + 1,
                movementPage.getTotalPages(),
                (int) movementPage.getTotalElements(),
                pageSize);

        return new StockMovementListResponseDto(movements, pagination);
    }

    // PRIVATE HELPER METHODS

    private void createTypeSpecificRecord(AnalogArticle article, String type, Object typeDetails) {
        switch (type.toLowerCase()) {
            case "vinyl":
                createVinylRecord(article, typeDetails);
                break;
            case "cassette":
                createCassetteRecord(article, typeDetails);
                break;
            case "cd":
                createCdRecord(article, typeDetails);
                break;
            default:
                throw new RuntimeException("Tipo de artículo no válido: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    private void createVinylRecord(AnalogArticle article, Object typeDetails) {
        Vinyl vinyl = new Vinyl();
        vinyl.setAnalogArticle(article);

        // Set default values
        vinyl.setRpm(33);
        vinyl.setIsLimitedEdition(false);

        if (typeDetails instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) typeDetails;

            if (details.containsKey("rpm")) {
                vinyl.setRpm((Integer) details.get("rpm"));
            }
            if (details.containsKey("isLimitedEdition")) {
                vinyl.setIsLimitedEdition((Boolean) details.get("isLimitedEdition"));
            }
            if (details.containsKey("remainingLimitedStock")) {
                vinyl.setRemainingLimitedStock((Integer) details.get("remainingLimitedStock"));
            }

            // Handle vinyl category
            if (details.containsKey("vinylCategoryId")) {
                Integer categoryId = (Integer) details.get("vinylCategoryId");
                if (categoryId != null) {
                    VinylCategory category = vinylCategoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Categoría de vinilo no encontrada"));
                    vinyl.setVinylCategory(category);
                }
            }

            // Handle special edition
            if (details.containsKey("vinylSpecialEditionId")) {
                Integer specialEditionId = (Integer) details.get("vinylSpecialEditionId");
                if (specialEditionId != null) {
                    VinylSpecialEdition specialEdition = vinylSpecialEditionRepository.findById(specialEditionId)
                            .orElseThrow(() -> new RuntimeException("Edición especial de vinilo no encontrada"));
                    vinyl.setVinylSpecialEdition(specialEdition);
                }
            }
        }

        // Use default category if none specified
        if (vinyl.getVinylCategory() == null) {
            VinylCategory defaultCategory = vinylCategoryRepository.findBySize("12\"")
                    .orElse(vinylCategoryRepository.findAll().stream().findFirst().orElse(null));
            vinyl.setVinylCategory(defaultCategory);
        }

        vinylRepository.save(vinyl);
        log.info("Created vinyl record for article ID: {}", article.getId());
    }

    @SuppressWarnings("unchecked")
    private void createCassetteRecord(AnalogArticle article, Object typeDetails) {
        Cassette cassette = new Cassette();
        cassette.setAnalogArticle(article);
        cassette.setIsChromeTape(false);

        if (typeDetails instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) typeDetails;

            if (details.containsKey("brand")) {
                cassette.setBrand((String) details.get("brand"));
            }
            if (details.containsKey("isChromeTape")) {
                cassette.setIsChromeTape((Boolean) details.get("isChromeTape"));
            }

            // Handle cassette category
            if (details.containsKey("cassetteCategoryId")) {
                Integer categoryId = (Integer) details.get("cassetteCategoryId");
                if (categoryId != null) {
                    CassetteCategory category = cassetteCategoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Categoría de cassette no encontrada"));
                    cassette.setCassetteCategory(category);
                }
            }
        }

        // Use default category if none specified
        if (cassette.getCassetteCategory() == null) {
            CassetteCategory defaultCategory = cassetteCategoryRepository.findByName("Nuevo")
                    .orElse(cassetteCategoryRepository.findAll().stream().findFirst().orElse(null));
            cassette.setCassetteCategory(defaultCategory);
        }

        cassetteRepository.save(cassette);
        log.info("Created cassette record for article ID: {}", article.getId());
    }

    @SuppressWarnings("unchecked")
    private void createCdRecord(AnalogArticle article, Object typeDetails) {
        Cd cd = new Cd();
        cd.setAnalogArticle(article);
        cd.setDiscCount(1);
        cd.setHasBonusContent(false);
        cd.setIsRemastered(false);

        if (typeDetails instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) typeDetails;

            if (details.containsKey("discCount")) {
                cd.setDiscCount((Integer) details.get("discCount"));
            }
            if (details.containsKey("hasBonusContent")) {
                cd.setHasBonusContent((Boolean) details.get("hasBonusContent"));
            }
            if (details.containsKey("isRemastered")) {
                cd.setIsRemastered((Boolean) details.get("isRemastered"));
            }
        }

        cdRepository.save(cd);
        log.info("Created CD record for article ID: {}", article.getId());
    }

    private void createInitialStockMovement(AnalogArticle article, Integer quantity, Integer adminUserId) {
        try {
            // Get "Entrada" movement type
            MovementType entryType = movementTypeRepository.findByName("Entrada")
                    .orElseThrow(() -> new RuntimeException("Tipo de movimiento 'Entrada' no encontrado"));

            // Get "Inventario Inicial" reference type
            MovementReferenceType initialInventory = movementReferenceTypeRepository.findByName("Inventario Inicial")
                    .orElseThrow(() -> new RuntimeException("Tipo de referencia 'Inventario Inicial' no encontrado"));

            // Get admin user
            User adminUser = userRepository.findById(adminUserId)
                    .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));

            StockMovement initialMovement = new StockMovement();
            initialMovement.setAnalogArticle(article);
            initialMovement.setMovementType(entryType);
            initialMovement.setMovementReferenceType(initialInventory);
            initialMovement.setQuantity(quantity);
            initialMovement.setNotes("Inventario inicial al crear artículo");
            initialMovement.setCreatedByUser(adminUser);

            stockMovementRepository.save(initialMovement);
            log.info("Created initial stock movement for article ID: {}", article.getId());

        } catch (Exception e) {
            log.warn("Could not create initial stock movement for article {}: {}",
                    article.getId(), e.getMessage());
            // Don't fail the entire transaction for initial stock movement
        }
    }

    private StockMovementListResponseDto.StockMovementItemDto mapToMovementItemDto(StockMovement movement) {
        StockMovementListResponseDto.StockMovementItemDto dto = new StockMovementListResponseDto.StockMovementItemDto();

        dto.setId(movement.getId());
        dto.setMovement_type(movement.getMovementType().getName());
        dto.setQuantity(movement.getQuantity());
        dto.setPrevious_stock(movement.getPreviousStock());
        dto.setNew_stock(movement.getNewStock());
        dto.setReference_type(movement.getMovementReferenceType().getName());
        dto.setReference_id(movement.getReferenceId());
        dto.setNotes(movement.getNotes());
        dto.setCreated_at(movement.getCreatedAt());

        // Article info
        if (movement.getAnalogArticle() != null) {
            AnalogArticle article = movement.getAnalogArticle();
            String type = determineArticleType(article.getId());

            dto.setArticle(new StockMovementListResponseDto.ArticleInfoDto(
                    article.getId(),
                    article.getTitle(),
                    type,
                    article.getArtist() != null ? article.getArtist().getName() : null));
        }

        // User info
        if (movement.getCreatedByUser() != null) {
            dto.setCreated_by(new StockMovementListResponseDto.UserInfoDto(
                    movement.getCreatedByUser().getId(),
                    movement.getCreatedByUser().getUsername()));
        }

        return dto;
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
}