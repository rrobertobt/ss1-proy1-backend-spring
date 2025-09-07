package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usac.cunoc.bpmn.dto.catalog.master.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.service.CatalogMasterService;

/**
 * Catalog master controller for master data endpoints
 */
@RestController
@RequestMapping("/api/v1/catalogs")
@RequiredArgsConstructor
@Tag(name = "Master Catalogs", description = "Master data catalog operations")
public class CatalogMasterController {

    private final CatalogMasterService catalogMasterService;

    @GetMapping("/genres")
    @Operation(summary = "Get music genres", description = "Get all available music genres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genres retrieved successfully")
    })
    public ResponseEntity<ApiResponseDto<GenreListResponseDto>> getGenres() {
        GenreListResponseDto genres = catalogMasterService.getGenres();
        return ResponseEntity.ok(ApiResponseDto.success(genres));
    }

    @GetMapping("/artists")
    @Operation(summary = "Get artists", description = "Get all available artists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artists retrieved successfully")
    })
    public ResponseEntity<ApiResponseDto<ArtistListResponseDto>> getArtists() {
        ArtistListResponseDto artists = catalogMasterService.getArtists();
        return ResponseEntity.ok(ApiResponseDto.success(artists));
    }

    @GetMapping("/vinyl-categories")
    @Operation(summary = "Get vinyl categories", description = "Get all available vinyl categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vinyl categories retrieved successfully")
    })
    public ResponseEntity<ApiResponseDto<VinylCategoryListResponseDto>> getVinylCategories() {
        VinylCategoryListResponseDto categories = catalogMasterService.getVinylCategories();
        return ResponseEntity.ok(ApiResponseDto.success(categories));
    }

    @GetMapping("/cassette-categories")
    @Operation(summary = "Get cassette categories", description = "Get all available cassette categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cassette categories retrieved successfully")
    })
    public ResponseEntity<ApiResponseDto<CassetteCategoryListResponseDto>> getCassetteCategories() {
        CassetteCategoryListResponseDto categories = catalogMasterService.getCassetteCategories();
        return ResponseEntity.ok(ApiResponseDto.success(categories));
    }

    @GetMapping("/vinyl-special-editions")
    @Operation(summary = "Get vinyl special editions", description = "Get all available vinyl special editions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vinyl special editions retrieved successfully")
    })
    public ResponseEntity<ApiResponseDto<VinylSpecialEditionListResponseDto>> getVinylSpecialEditions() {
        VinylSpecialEditionListResponseDto specialEditions = catalogMasterService.getVinylSpecialEditions();
        return ResponseEntity.ok(ApiResponseDto.success(specialEditions));
    }

    @GetMapping("/currencies")
    @Operation(summary = "Get currencies", description = "Get all available currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currencies retrieved successfully")
    })
    public ResponseEntity<ApiResponseDto<CurrencyListResponseDto>> getCurrencies() {
        CurrencyListResponseDto currencies = catalogMasterService.getCurrencies();
        return ResponseEntity.ok(ApiResponseDto.success(currencies));
    }

    @GetMapping("/countries")
    @Operation(summary = "Get countries", description = "Get all available countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    })
    public ResponseEntity<ApiResponseDto<CountryListResponseDto>> getCountries() {
        CountryListResponseDto countries = catalogMasterService.getCountries();
        return ResponseEntity.ok(ApiResponseDto.success(countries));
    }
}