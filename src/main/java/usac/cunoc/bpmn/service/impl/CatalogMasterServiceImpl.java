package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.catalog.master.*;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.CatalogMasterService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Catalog master service implementation for master data operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogMasterServiceImpl implements CatalogMasterService {

    private final MusicGenreRepository musicGenreRepository;
    private final ArtistRepository artistRepository;
    private final VinylCategoryRepository vinylCategoryRepository;
    private final CassetteCategoryRepository cassetteCategoryRepository;
    private final VinylSpecialEditionRepository vinylSpecialEditionRepository;
    private final CurrencyRepository currencyRepository;
    private final CountryRepository countryRepository;

    @Override
    public GenreListResponseDto getGenres() {
        List<MusicGenre> genres = musicGenreRepository.findAllByOrderByNameAsc();

        List<GenreListResponseDto.GenreDto> genreDtos = genres.stream()
                .map(genre -> new GenreListResponseDto.GenreDto(
                        genre.getId(),
                        genre.getName(),
                        genre.getDescription()))
                .collect(Collectors.toList());

        return new GenreListResponseDto(genreDtos);
    }

    @Override
    public ArtistListResponseDto getArtists() {
        List<Artist> artists = artistRepository.findAll();

        List<ArtistListResponseDto.ArtistDto> artistDtos = artists.stream()
                .map(artist -> new ArtistListResponseDto.ArtistDto(
                        artist.getId(),
                        artist.getName(),
                        artist.getIsBand(),
                        artist.getWebsite()))
                .collect(Collectors.toList());

        return new ArtistListResponseDto(artistDtos);
    }

    @Override
    public VinylCategoryListResponseDto getVinylCategories() {
        List<VinylCategory> categories = vinylCategoryRepository.findAll();

        List<VinylCategoryListResponseDto.VinylCategoryDto> categoryDtos = categories.stream()
                .map(category -> new VinylCategoryListResponseDto.VinylCategoryDto(
                        category.getId(),
                        category.getSize(),
                        category.getDescription(),
                        category.getTypicalRpm()))
                .collect(Collectors.toList());

        return new VinylCategoryListResponseDto(categoryDtos);
    }

    @Override
    public CassetteCategoryListResponseDto getCassetteCategories() {
        List<CassetteCategory> categories = cassetteCategoryRepository.findAll();

        List<CassetteCategoryListResponseDto.CassetteCategoryDto> categoryDtos = categories.stream()
                .map(category -> new CassetteCategoryListResponseDto.CassetteCategoryDto(
                        category.getId(),
                        category.getName(),
                        category.getDiscountPercentage(),
                        category.getDescription()))
                .collect(Collectors.toList());

        return new CassetteCategoryListResponseDto(categoryDtos);
    }

    @Override
    public VinylSpecialEditionListResponseDto getVinylSpecialEditions() {
        List<VinylSpecialEdition> specialEditions = vinylSpecialEditionRepository.findAll();

        List<VinylSpecialEditionListResponseDto.VinylSpecialEditionDto> editionDtos = specialEditions.stream()
                .map(edition -> new VinylSpecialEditionListResponseDto.VinylSpecialEditionDto(
                        edition.getId(),
                        edition.getName(),
                        edition.getColor(),
                        edition.getMaterialDescription(),
                        edition.getExtraContent(),
                        edition.getIsLimited(),
                        edition.getLimitedQuantity()))
                .collect(Collectors.toList());

        return new VinylSpecialEditionListResponseDto(editionDtos);
    }

    @Override
    public CurrencyListResponseDto getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();

        List<CurrencyListResponseDto.CurrencyDto> currencyDtos = currencies.stream()
                .map(currency -> new CurrencyListResponseDto.CurrencyDto(
                        currency.getId(),
                        currency.getCode(),
                        currency.getName(),
                        currency.getSymbol()))
                .collect(Collectors.toList());

        return new CurrencyListResponseDto(currencyDtos);
    }

    @Override
    public CountryListResponseDto getCountries() {
        List<Country> countries = countryRepository.findAll();

        List<CountryListResponseDto.CountryDto> countryDtos = countries.stream()
                .map(country -> {
                    CountryListResponseDto.CurrencyDto currencyDto = null;
                    if (country.getCurrency() != null) {
                        currencyDto = new CountryListResponseDto.CurrencyDto(
                                country.getCurrency().getId(),
                                country.getCurrency().getCode(),
                                country.getCurrency().getSymbol());
                    }

                    return new CountryListResponseDto.CountryDto(
                            country.getId(),
                            country.getName(),
                            country.getCountryCode(),
                            currencyDto);
                })
                .collect(Collectors.toList());

        return new CountryListResponseDto(countryDtos);
    }
}