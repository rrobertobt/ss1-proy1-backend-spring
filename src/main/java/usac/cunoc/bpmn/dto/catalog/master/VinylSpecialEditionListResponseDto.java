package usac.cunoc.bpmn.dto.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Vinyl special edition list response DTO for master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for vinyl special editions master data")
public class VinylSpecialEditionListResponseDto {

    @Schema(description = "List of vinyl special editions")
    private List<VinylSpecialEditionDto> specialEditions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Vinyl special edition information")
    public static class VinylSpecialEditionDto {
        @Schema(description = "Special edition ID", example = "1")
        private Integer id;

        @Schema(description = "Edition name", example = "180g Audiophile")
        private String name;

        @Schema(description = "Color", example = "Clear")
        private String color;

        @Schema(description = "Material description")
        private String materialDescription;

        @Schema(description = "Extra content")
        private String extraContent;

        @Schema(description = "Is limited edition", example = "true")
        private Boolean isLimited;

        @Schema(description = "Limited quantity", example = "500")
        private Integer limitedQuantity;
    }
}