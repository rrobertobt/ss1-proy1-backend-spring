package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.promotion.PromotionListResponseDto;

/**
 * Promotion service interface for public promotion operations
 */
public interface PromotionService {

    /**
     * Get available CD promotions with filters
     * 
     * @param type     Promotion type filter ("Por Genero" or "Aleatorio")
     * @param genreId  Genre ID filter (only for genre-based promotions)
     * @param isActive Active status filter
     * @return List of available promotions
     */
    PromotionListResponseDto getCdPromotions(String type, Integer genreId, Boolean isActive);
}