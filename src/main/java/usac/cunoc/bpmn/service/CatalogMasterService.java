package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.catalog.master.*;

/**
 * Catalog master service interface for master data operations
 */
public interface CatalogMasterService {

    /**
     * Get all music genres
     */
    GenreListResponseDto getGenres();

    /**
     * Get all artists
     */
    ArtistListResponseDto getArtists();

    /**
     * Get all vinyl categories
     */
    VinylCategoryListResponseDto getVinylCategories();

    /**
     * Get all cassette categories
     */
    CassetteCategoryListResponseDto getCassetteCategories();

    /**
     * Get all vinyl special editions
     */
    VinylSpecialEditionListResponseDto getVinylSpecialEditions();

    /**
     * Get all currencies
     */
    CurrencyListResponseDto getCurrencies();

    /**
     * Get all countries
     */
    CountryListResponseDto getCountries();
}