package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.admin.catalog.master.*;
import usac.cunoc.bpmn.entity.Artist;
import usac.cunoc.bpmn.entity.Country;
import usac.cunoc.bpmn.entity.MusicGenre;
import usac.cunoc.bpmn.entity.VinylSpecialEdition;
import usac.cunoc.bpmn.exception.ResourceNotFoundException;
import usac.cunoc.bpmn.exception.DuplicateResourceException;
import usac.cunoc.bpmn.repository.ArtistRepository;
import usac.cunoc.bpmn.repository.CountryRepository;
import usac.cunoc.bpmn.repository.MusicGenreRepository;
import usac.cunoc.bpmn.repository.VinylSpecialEditionRepository;
import usac.cunoc.bpmn.service.AdminCatalogMasterService;
import java.time.LocalDateTime;

/**
 * Admin catalog master service implementation - 100% compliant with database
 * schema
 * and PDF specification
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCatalogMasterServiceImpl implements AdminCatalogMasterService {

    private final ArtistRepository artistRepository;
    private final CountryRepository countryRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final VinylSpecialEditionRepository vinylSpecialEditionRepository;

    @Override
    public CreateArtistResponseDto createArtist(CreateArtistRequestDto request, Integer adminUserId) {
        log.info("Creating new artist: {} by admin user: {}", request.getName(), adminUserId);

        // Validate unique name
        if (artistRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Artist with name '" + request.getName() + "' already exists");
        }

        // Validate country exists
        Country country = countryRepository.findById(request.getCountry_id())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Country not found with ID: " + request.getCountry_id()));

        // Create new artist entity
        Artist artist = new Artist();
        artist.setName(request.getName());
        artist.setBiography(request.getBiography());
        artist.setFormationDate(request.getFormation_date());
        artist.setCareerStartDate(request.getCareer_start_date());
        artist.setCountry(country);
        artist.setIsBand(request.getIs_band());
        artist.setWebsite(request.getWebsite());
        artist.setTotalSales(0);

        // Save artist
        Artist savedArtist = artistRepository.save(artist);

        log.info("Artist created successfully with ID: {}", savedArtist.getId());

        // Build response
        return new CreateArtistResponseDto(
                savedArtist.getId(),
                savedArtist.getName(),
                savedArtist.getBiography(),
                savedArtist.getFormationDate(),
                savedArtist.getCareerStartDate(),
                new CreateArtistResponseDto.CountryDto(
                        country.getId(),
                        country.getName()),
                savedArtist.getIsBand(),
                savedArtist.getWebsite(),
                savedArtist.getCreatedAt());
    }

    @Override
    public UpdateArtistResponseDto updateArtist(Integer artistId, UpdateArtistRequestDto request, Integer adminUserId) {
        log.info("Updating artist ID: {} by admin user: {}", artistId, adminUserId);

        // Find existing artist
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));

        // Validate unique name (excluding current artist)
        if (artistRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), artistId)) {
            throw new DuplicateResourceException("Artist with name '" + request.getName() + "' already exists");
        }

        // Validate country exists
        Country country = countryRepository.findById(request.getCountry_id())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Country not found with ID: " + request.getCountry_id()));

        // Update artist entity
        artist.setName(request.getName());
        artist.setBiography(request.getBiography());
        artist.setFormationDate(request.getFormation_date());
        artist.setCareerStartDate(request.getCareer_start_date());
        artist.setCountry(country);
        artist.setIsBand(request.getIs_band());
        artist.setWebsite(request.getWebsite());

        // Save updated artist
        Artist updatedArtist = artistRepository.save(artist);

        log.info("Artist updated successfully with ID: {}", updatedArtist.getId());

        // Build response
        return new UpdateArtistResponseDto(
                updatedArtist.getId(),
                updatedArtist.getName(),
                updatedArtist.getUpdatedAt());
    }

    @Override
    public DeleteArtistResponseDto deleteArtist(Integer artistId, Integer adminUserId) {
        log.info("Deleting artist ID: {} by admin user: {}", artistId, adminUserId);

        // Find existing artist
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));

        // Store artist data before deletion for response
        String artistName = artist.getName();
        LocalDateTime deletionTime = LocalDateTime.now();

        // Soft delete: In this case, we'll actually delete since there's no soft delete
        // field
        // But we could add an is_active field to the artist table for soft delete
        artistRepository.delete(artist);

        log.info("Artist deleted successfully with ID: {}", artistId);

        // Build response
        return new DeleteArtistResponseDto(
                artistId,
                artistName,
                true,
                deletionTime);
    }

    @Override
    public CreateGenreResponseDto createGenre(CreateGenreRequestDto request, Integer adminUserId) {
        log.info("Creating new genre: {} by admin user: {}", request.getName(), adminUserId);

        // Validate unique name
        if (musicGenreRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Genre with name '" + request.getName() + "' already exists");
        }

        // Create new genre entity
        MusicGenre genre = new MusicGenre();
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());

        // Save genre
        MusicGenre savedGenre = musicGenreRepository.save(genre);

        log.info("Genre created successfully with ID: {}", savedGenre.getId());

        // Build response
        return new CreateGenreResponseDto(
                savedGenre.getId(),
                savedGenre.getName(),
                savedGenre.getDescription(),
                savedGenre.getCreatedAt());
    }

    @Override
    public CreateVinylSpecialEditionResponseDto createVinylSpecialEdition(CreateVinylSpecialEditionRequestDto request,
            Integer adminUserId) {
        log.info("Creating new vinyl special edition: {} by admin user: {}", request.getName(), adminUserId);

        // Validate unique name
        if (vinylSpecialEditionRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Vinyl special edition with name '" + request.getName() + "' already exists");
        }

        // Validate business rules - enhanced validation
        if (Boolean.TRUE.equals(request.getIs_limited())) {
            if (request.getLimited_quantity() == null || request.getLimited_quantity() <= 0) {
                throw new IllegalArgumentException("Las ediciones limitadas deben tener una cantidad válida mayor a 0");
            }
            if (request.getLimited_quantity() > 10000) {
                throw new IllegalArgumentException("La cantidad limitada no puede exceder 10,000 unidades");
            }
        }

        if (Boolean.FALSE.equals(request.getIs_limited()) && request.getLimited_quantity() != null
                && request.getLimited_quantity() > 0) {
            throw new IllegalArgumentException("Las ediciones no limitadas no pueden tener una cantidad limitada");
        }

        // Create new vinyl special edition entity
        VinylSpecialEdition edition = new VinylSpecialEdition();
        edition.setName(request.getName());
        edition.setColor(request.getColor());
        edition.setMaterialDescription(request.getMaterial_description());
        edition.setExtraContent(request.getExtra_content());
        edition.setIsLimited(request.getIs_limited());
        edition.setLimitedQuantity(request.getLimited_quantity());

        // Save vinyl special edition
        VinylSpecialEdition savedEdition = vinylSpecialEditionRepository.save(edition);

        log.info("Vinyl special edition created successfully with ID: {}", savedEdition.getId());

        // Build response
        return new CreateVinylSpecialEditionResponseDto(
                savedEdition.getId(),
                savedEdition.getName(),
                savedEdition.getColor(),
                savedEdition.getMaterialDescription(),
                savedEdition.getExtraContent(),
                savedEdition.getIsLimited(),
                savedEdition.getLimitedQuantity(),
                savedEdition.getCreatedAt());
    }
}