package usac.cunoc.bpmn.dto.admin.catalog.master;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Create vinyl special edition response DTO - matches PDF JSON structure
 * exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when creating a new vinyl special edition")
public class CreateVinylSpecialEditionResponseDto {

    @Schema(description = "Special edition ID", example = "6")
    private Integer id;

    @Schema(description = "Special edition name", example = "Edición Holográfica")
    private String name;

    @Schema(description = "Vinyl color", example = "Multicolor holográfico")
    private String color;

    @Schema(description = "Material description", example = "Vinilo con acabado holográfico especial")
    private String materialDescription;

    @Schema(description = "Extra content included", example = "Poster holográfico y stickers exclusivos")
    private String extraContent;

    @Schema(description = "Whether this is a limited edition", example = "true")
    private Boolean isLimited;

    @Schema(description = "Limited quantity", example = "150")
    private Integer limitedQuantity;

    @Schema(description = "Creation timestamp", example = "2025-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}