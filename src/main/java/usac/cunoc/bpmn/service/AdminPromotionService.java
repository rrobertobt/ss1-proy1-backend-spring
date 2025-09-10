package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.promotion.CreatePromotionRequestDto;
import usac.cunoc.bpmn.dto.promotion.CreatePromotionResponseDto;
import usac.cunoc.bpmn.dto.promotion.UpdatePromotionRequestDto;
import usac.cunoc.bpmn.dto.promotion.UpdatePromotionResponseDto;

/**
 * Admin promotion service interface for administrative promotion operations
 */
public interface AdminPromotionService {

    /**
     * Create new CD promotion
     * 
     * @param request     Create promotion request
     * @param adminUserId Admin user ID creating the promotion
     * @return Created promotion details
     */
    CreatePromotionResponseDto createCdPromotion(CreatePromotionRequestDto request, Integer adminUserId);

    /**
     * Update existing CD promotion
     * 
     * @param promotionId Promotion ID to update
     * @param request     Update promotion request
     * @param adminUserId Admin user ID updating the promotion
     * @return Updated promotion details
     */
    UpdatePromotionResponseDto updateCdPromotion(Integer promotionId, UpdatePromotionRequestDto request,
            Integer adminUserId);

    /**
     * Delete CD promotion
     * 
     * @param promotionId Promotion ID to delete
     * @param adminUserId Admin user ID deleting the promotion
     */
    void deleteCdPromotion(Integer promotionId, Integer adminUserId);
}