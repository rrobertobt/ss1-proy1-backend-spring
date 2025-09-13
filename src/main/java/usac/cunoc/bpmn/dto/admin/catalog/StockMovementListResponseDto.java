package usac.cunoc.bpmn.dto.admin.catalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Stock movement list response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for stock movements list")
public class StockMovementListResponseDto {

    @Schema(description = "List of stock movements")
    private List<StockMovementItemDto> movements;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Stock movement item information")
    public static class StockMovementItemDto {
        @Schema(description = "Movement ID", example = "1")
        private Integer id;

        @Schema(description = "Article information")
        private ArticleInfoDto article;

        @Schema(description = "Movement type", example = "Entrada")
        private String movement_type;

        @Schema(description = "Quantity moved", example = "10")
        private Integer quantity;

        @Schema(description = "Previous stock", example = "15")
        private Integer previous_stock;

        @Schema(description = "New stock", example = "25")
        private Integer new_stock;

        @Schema(description = "Reference type", example = "Ajuste Manual")
        private String reference_type;

        @Schema(description = "Reference ID", example = "null")
        private Integer reference_id;

        @Schema(description = "Movement notes")
        private String notes;

        @Schema(description = "Created by user")
        private UserInfoDto created_by;

        @Schema(description = "Movement creation timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime created_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article information")
    public static class ArticleInfoDto {
        @Schema(description = "Article ID", example = "1")
        private Integer id;

        @Schema(description = "Article title", example = "Abbey Road")
        private String title;

        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Artist name", example = "The Beatles")
        private String artist;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User information")
    public static class UserInfoDto {
        @Schema(description = "User ID", example = "1")
        private Integer id;

        @Schema(description = "Username", example = "admin")
        private String username;
    }
}