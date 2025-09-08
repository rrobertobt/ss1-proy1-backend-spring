package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.admin.catalog.master.*;

/**
 * Admin catalog master service interface for administrative catalog operations
 */
public interface AdminCatalogMasterService {

    /**
     * Create a new artist
     * 
     * @param request     Artist creation request data
     * @param adminUserId ID of the admin user performing the operation
     * @return Created artist response data
     */
    CreateArtistResponseDto createArtist(CreateArtistRequestDto request, Integer adminUserId);

    /**
     * Update an existing artist
     * 
     * @param artistId    ID of the artist to update
     * @param request     Artist update request data
     * @param adminUserId ID of the admin user performing the operation
     * @return Updated artist response data
     */
    UpdateArtistResponseDto updateArtist(Integer artistId, UpdateArtistRequestDto request, Integer adminUserId);

    /**
     * Delete an artist (soft delete)
     * 
     * @param artistId    ID of the artist to delete
     * @param adminUserId ID of the admin user performing the operation
     * @return Deletion confirmation response data
     */
    DeleteArtistResponseDto deleteArtist(Integer artistId, Integer adminUserId);

    /**
     * Create a new music genre
     * 
     * @param request     Genre creation request data
     * @param adminUserId ID of the admin user performing the operation
     * @return Created genre response data
     */
    CreateGenreResponseDto createGenre(CreateGenreRequestDto request, Integer adminUserId);

    /**
     * Create a new vinyl special edition
     * 
     * @param request     Vinyl special edition creation request data
     * @param adminUserId ID of the admin user performing the operation
     * @return Created vinyl special edition response data
     */
    CreateVinylSpecialEditionResponseDto createVinylSpecialEdition(CreateVinylSpecialEditionRequestDto request,
            Integer adminUserId);
}