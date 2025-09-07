package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.catalog.*;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.CatalogService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Catalog service implementation - 100% compliant with PDF JSON structure
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private final AnalogArticleRepository analogArticleRepository;
    private final ArtistRepository artistRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRatingRepository articleRatingRepository;
    private final VinylRepository vinylRepository;
    private final CassetteRepository cassetteRepository;
    private final CdRepository cdRepository;

    @Override
    public CatalogArticlesResponseDto getArticles(
            Integer page, Integer limit, String search, Integer genreId,
            Integer artistId, String minPrice, String maxPrice, String type,
            String sortBy, Boolean availability) {

        // Default pagination values
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = limit != null && limit > 0 ? limit : 12;

        // Parse price filters
        BigDecimal minPriceValue = parsePrice(minPrice);
        BigDecimal maxPriceValue = parsePrice(maxPrice);

        // Build sort
        Sort sort = buildSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Get articles with filters
        Page<AnalogArticle> articlePage;
        if (type != null && !type.isEmpty()) {
            articlePage = getArticlesByType(type, search, genreId, artistId,
                    minPriceValue, maxPriceValue, pageable);
        } else {
            articlePage = analogArticleRepository.findWithFilters(
                    search, genreId, artistId, minPriceValue, maxPriceValue, pageable);
        }

        // Map to DTOs
        List<CatalogArticleDto> articles = articlePage.getContent().stream()
                .map(this::mapToArticleDto)
                .collect(Collectors.toList());

        // Build pagination
        PaginationDto pagination = new PaginationDto(
                pageNumber + 1,
                articlePage.getTotalPages(),
                (int) articlePage.getTotalElements(),
                pageSize);

        // Build filters
        CatalogArticlesResponseDto.FiltersDto filters = buildFilters();

        return new CatalogArticlesResponseDto(articles, pagination, filters);
    }

    @Override
    public ArticleDetailResponseDto getArticleById(Integer articleId) {
        AnalogArticle article = analogArticleRepository.findByIdWithDetails(articleId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        return mapToArticleDetailDto(article);
    }

    @Override
    public ArticleCommentsResponseDto getArticleComments(Integer articleId, Integer page, Integer limit) {
        // Verify article exists
        if (!analogArticleRepository.existsById(articleId)) {
            throw new RuntimeException("Artículo no encontrado");
        }

        // Default pagination
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = limit != null && limit > 0 ? limit : 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Get top-level comments
        Page<ArticleComment> commentPage = articleCommentRepository
                .findTopLevelCommentsByArticleId(articleId, pageable);

        // Map to DTOs with replies
        List<ArticleCommentDto> comments = commentPage.getContent().stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());

        // Get total comments count
        Integer totalComments = articleCommentRepository.countVisibleCommentsByArticleId(articleId);

        // Build pagination
        PaginationDto pagination = new PaginationDto(
                pageNumber + 1,
                commentPage.getTotalPages(),
                (int) commentPage.getTotalElements(),
                pageSize);

        return new ArticleCommentsResponseDto(articleId, totalComments, comments, pagination);
    }

    @Override
    public ArticleRatingsResponseDto getArticleRatings(Integer articleId, Integer page,
            Integer limit, String sortBy) {
        // Verify article exists
        if (!analogArticleRepository.existsById(articleId)) {
            throw new RuntimeException("Artículo no encontrado");
        }

        // Default pagination
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = limit != null && limit > 0 ? limit : 10;

        // Build sort for ratings
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("helpful".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "helpfulVotes").and(sort);
        } else if ("rating".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "rating").and(sort);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Get ratings
        Page<ArticleRating> ratingPage = articleRatingRepository
                .findRatingsByArticleId(articleId, pageable);

        // Map to DTOs
        List<ArticleRatingDto> ratings = ratingPage.getContent().stream()
                .map(this::mapToRatingDto)
                .collect(Collectors.toList());

        // Get rating statistics
        BigDecimal averageRating = articleRatingRepository.findAverageRatingByArticleId(articleId);
        Integer totalRatings = articleRatingRepository.countRatingsByArticleId(articleId);
        Map<String, Integer> ratingDistribution = buildRatingDistribution(articleId);

        // Build pagination
        PaginationDto pagination = new PaginationDto(
                pageNumber + 1,
                ratingPage.getTotalPages(),
                (int) ratingPage.getTotalElements(),
                pageSize);

        return new ArticleRatingsResponseDto(
                articleId,
                averageRating != null ? averageRating : BigDecimal.ZERO,
                totalRatings != null ? totalRatings : 0,
                ratingDistribution,
                ratings,
                pagination);
    }

    // PRIVATE HELPER METHODS

    private Page<AnalogArticle> getArticlesByType(String type, String search, Integer genreId,
            Integer artistId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        switch (type.toLowerCase()) {
            case "vinyl":
                return analogArticleRepository.findVinylsAvailable(pageable);
            case "cassette":
                return analogArticleRepository.findCassettesAvailable(pageable);
            case "cd":
                return analogArticleRepository.findCdsAvailable(pageable);
            default:
                return analogArticleRepository.findWithFilters(
                        search, genreId, artistId, minPrice, maxPrice, pageable);
        }
    }

    private Sort buildSort(String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        switch (sortBy) {
            case "price_asc":
                return Sort.by(Sort.Direction.ASC, "price");
            case "price_desc":
                return Sort.by(Sort.Direction.DESC, "price");
            case "rating":
                return Sort.by(Sort.Direction.DESC, "averageRating");
            case "popular":
                return Sort.by(Sort.Direction.DESC, "totalSold");
            case "name_asc":
                return Sort.by(Sort.Direction.ASC, "title");
            case "name_desc":
                return Sort.by(Sort.Direction.DESC, "title");
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }

    private BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(priceStr.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid price format: {}", priceStr);
            return null;
        }
    }

    private CatalogArticleDto mapToArticleDto(AnalogArticle article) {
        CatalogArticleDto dto = new CatalogArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setPrice(article.getPrice());
        dto.setImageUrl(article.getImageUrl());
        dto.setAverageRating(article.getAverageRating());
        dto.setTotalRatings(article.getTotalRatings());
        dto.setStockQuantity(article.getStockQuantity());
        dto.setIsAvailable(article.getIsAvailable());
        dto.setIsPreorder(article.getIsPreorder());

        // Artist info
        if (article.getArtist() != null) {
            dto.setArtist(new CatalogArticleDto.ArtistDto(
                    article.getArtist().getId(),
                    article.getArtist().getName()));
        }

        // Currency info
        if (article.getCurrency() != null) {
            dto.setCurrency(new CatalogArticleDto.CurrencyDto(
                    article.getCurrency().getCode(),
                    article.getCurrency().getSymbol()));
        }

        // Genre info
        if (article.getMusicGenre() != null) {
            dto.setGenre(new CatalogArticleDto.GenreDto(
                    article.getMusicGenre().getId(),
                    article.getMusicGenre().getName()));
        }

        // Determine article type
        dto.setType(determineArticleType(article.getId()));

        return dto;
    }

    private ArticleDetailResponseDto mapToArticleDetailDto(AnalogArticle article) {
        ArticleDetailResponseDto dto = new ArticleDetailResponseDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setPrice(article.getPrice());
        dto.setReleaseDate(article.getReleaseDate());
        dto.setDescription(article.getDescription());
        dto.setDimensions(article.getDimensions());
        dto.setWeightGrams(article.getWeightGrams());
        dto.setBarcode(article.getBarcode());
        dto.setImageUrl(article.getImageUrl());
        dto.setAverageRating(article.getAverageRating());
        dto.setTotalRatings(article.getTotalRatings());
        dto.setStockQuantity(article.getStockQuantity());
        dto.setIsAvailable(article.getIsAvailable());
        dto.setIsPreorder(article.getIsPreorder());
        dto.setPreorderReleaseDate(article.getPreorderReleaseDate());
        dto.setPreorderEndDate(article.getPreorderEndDate());

        // Artist detailed info
        if (article.getArtist() != null) {
            Artist artist = article.getArtist();
            dto.setArtist(new ArticleDetailResponseDto.ArtistDetailDto(
                    artist.getId(),
                    artist.getName(),
                    artist.getBiography(),
                    artist.getFormationDate(),
                    artist.getCareerStartDate(),
                    artist.getIsBand(),
                    artist.getWebsite()));
        }

        // Currency info
        if (article.getCurrency() != null) {
            dto.setCurrency(new ArticleDetailResponseDto.CurrencyDto(
                    article.getCurrency().getCode(),
                    article.getCurrency().getSymbol()));
        }

        // Genre detailed info
        if (article.getMusicGenre() != null) {
            MusicGenre genre = article.getMusicGenre();
            dto.setGenre(new ArticleDetailResponseDto.GenreDto(
                    genre.getId(),
                    genre.getName(),
                    genre.getDescription()));
        }

        // Type and type-specific details
        String type = determineArticleType(article.getId());
        dto.setType(type);
        dto.setTypeDetails(getTypeSpecificDetails(article.getId(), type));

        return dto;
    }

    private ArticleCommentDto mapToCommentDto(ArticleComment comment) {
        ArticleCommentDto dto = new ArticleCommentDto();
        dto.setId(comment.getId());
        dto.setCommentText(comment.getCommentText());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        dto.setLikesCount(comment.getLikesCount());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        // User info
        if (comment.getUser() != null) {
            dto.setUser(new ArticleCommentDto.UserDto(
                    comment.getUser().getId(),
                    comment.getUser().getUsername()));
        }

        // Status info
        if (comment.getCommentStatus() != null) {
            dto.setStatus(new ArticleCommentDto.StatusDto(
                    comment.getCommentStatus().getId(),
                    comment.getCommentStatus().getName()));
        }

        // Get replies
        List<ArticleComment> replies = articleCommentRepository
                .findRepliesByParentCommentId(comment.getId());
        dto.setReplies(replies.stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private ArticleRatingDto mapToRatingDto(ArticleRating rating) {
        ArticleRatingDto dto = new ArticleRatingDto();
        dto.setId(rating.getId());
        dto.setRating(rating.getRating());
        dto.setReviewText(rating.getReviewText());
        dto.setIsVerifiedPurchase(rating.getIsVerifiedPurchase());
        dto.setHelpfulVotes(rating.getHelpfulVotes());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUpdatedAt(rating.getUpdatedAt());

        // User info
        if (rating.getUser() != null) {
            dto.setUser(new ArticleRatingDto.UserDto(
                    rating.getUser().getId(),
                    rating.getUser().getUsername()));
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

    private Object getTypeSpecificDetails(Integer articleId, String type) {
        switch (type) {
            case "vinyl":
                return vinylRepository.findByAnalogArticleIdWithDetails(articleId)
                        .map(this::mapVinylDetails)
                        .orElse(null);
            case "cassette":
                return cassetteRepository.findByAnalogArticleIdWithDetails(articleId)
                        .map(this::mapCassetteDetails)
                        .orElse(null);
            case "cd":
                return cdRepository.findByAnalogArticleIdWithDetails(articleId)
                        .map(this::mapCdDetails)
                        .orElse(null);
            default:
                return null;
        }
    }

    private Object mapVinylDetails(Vinyl vinyl) {
        Map<String, Object> details = new HashMap<>();
        details.put("rpm", vinyl.getRpm());
        details.put("isLimitedEdition", vinyl.getIsLimitedEdition());
        details.put("remainingLimitedStock", vinyl.getRemainingLimitedStock());

        if (vinyl.getVinylCategory() != null) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", vinyl.getVinylCategory().getId());
            category.put("size", vinyl.getVinylCategory().getSize());
            category.put("description", vinyl.getVinylCategory().getDescription());
            details.put("category", category);
        }

        if (vinyl.getVinylSpecialEdition() != null) {
            Map<String, Object> specialEdition = new HashMap<>();
            specialEdition.put("id", vinyl.getVinylSpecialEdition().getId());
            specialEdition.put("name", vinyl.getVinylSpecialEdition().getName());
            specialEdition.put("color", vinyl.getVinylSpecialEdition().getColor());
            specialEdition.put("materialDescription", vinyl.getVinylSpecialEdition().getMaterialDescription());
            details.put("specialEdition", specialEdition);
        }

        return details;
    }

    private Object mapCassetteDetails(Cassette cassette) {
        Map<String, Object> details = new HashMap<>();
        details.put("brand", cassette.getBrand());
        details.put("isChromeTape", cassette.getIsChromeTape());

        if (cassette.getCassetteCategory() != null) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", cassette.getCassetteCategory().getId());
            category.put("name", cassette.getCassetteCategory().getName());
            category.put("discountPercentage", cassette.getCassetteCategory().getDiscountPercentage());
            category.put("description", cassette.getCassetteCategory().getDescription());
            details.put("category", category);
        }

        return details;
    }

    private Object mapCdDetails(Cd cd) {
        Map<String, Object> details = new HashMap<>();
        details.put("discCount", cd.getDiscCount());
        details.put("hasBonusContent", cd.getHasBonusContent());
        details.put("isRemastered", cd.getIsRemastered());
        return details;
    }

    private CatalogArticlesResponseDto.FiltersDto buildFilters() {
        // Get available genres
        List<MusicGenre> genres = musicGenreRepository.findGenresWithAvailableArticles();
        List<CatalogArticlesResponseDto.GenreFilterDto> genreFilters = genres.stream()
                .map(g -> new CatalogArticlesResponseDto.GenreFilterDto(g.getId(), g.getName()))
                .collect(Collectors.toList());

        // Get available artists
        List<Artist> artists = artistRepository.findArtistsWithAvailableArticles();
        List<CatalogArticlesResponseDto.ArtistFilterDto> artistFilters = artists.stream()
                .map(a -> new CatalogArticlesResponseDto.ArtistFilterDto(a.getId(), a.getName()))
                .collect(Collectors.toList());

        // Get price range
        Object[] priceRange = analogArticleRepository.findPriceRange();
        CatalogArticlesResponseDto.PriceRangeDto priceRangeDto = null;
        if (priceRange != null && priceRange.length == 2 && priceRange[0] != null && priceRange[1] != null) {
            priceRangeDto = new CatalogArticlesResponseDto.PriceRangeDto(
                    (BigDecimal) priceRange[0],
                    (BigDecimal) priceRange[1]);
        }

        return new CatalogArticlesResponseDto.FiltersDto(genreFilters, artistFilters, priceRangeDto);
    }

    private Map<String, Integer> buildRatingDistribution(Integer articleId) {
        Object[][] distribution = articleRatingRepository.findRatingDistributionByArticleId(articleId);
        Map<String, Integer> result = new HashMap<>();

        // Initialize all ratings to 0
        for (int i = 1; i <= 5; i++) {
            result.put(String.valueOf(i), 0);
        }

        // Fill with actual values
        if (distribution != null) {
            for (Object[] row : distribution) {
                Integer rating = (Integer) row[0];
                Long count = (Long) row[1];
                result.put(String.valueOf(rating), count.intValue());
            }
        }

        return result;
    }
}