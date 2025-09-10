package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.rating.CreateRatingRequestDto;
import usac.cunoc.bpmn.dto.rating.CreateRatingResponseDto;
import usac.cunoc.bpmn.dto.rating.DeleteRatingResponseDto;
import usac.cunoc.bpmn.dto.rating.UpdateRatingRequestDto;
import usac.cunoc.bpmn.dto.rating.UpdateRatingResponseDto;

/**
 * Service interface for article rating operations
 * Handles CRUD operations for article ratings and reviews
 */
public interface ArticleRatingService {

    /**
     * Create a new rating for an article
     * 
     * @param articleId Article to rate
     * @param request   Rating data
     * @param userId    Current user ID
     * @return Created rating response
     */
    CreateRatingResponseDto createRating(Integer articleId, CreateRatingRequestDto request, Integer userId);

    /**
     * Update an existing rating
     * 
     * @param articleId Article ID
     * @param ratingId  Rating ID to update
     * @param request   Updated rating data
     * @param userId    Current user ID
     * @return Updated rating response
     */
    UpdateRatingResponseDto updateRating(Integer articleId, Integer ratingId,
            UpdateRatingRequestDto request, Integer userId);

    /**
     * Delete a rating
     * 
     * @param articleId Article ID
     * @param ratingId  Rating ID to delete
     * @param userId    Current user ID
     * @return Deletion confirmation response
     */
    DeleteRatingResponseDto deleteRating(Integer articleId, Integer ratingId, Integer userId);
}