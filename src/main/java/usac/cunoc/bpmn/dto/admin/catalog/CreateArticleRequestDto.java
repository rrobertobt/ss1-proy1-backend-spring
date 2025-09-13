package usac.cunoc.bpmn.dto.admin.catalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Create article request DTO - matches PDF JSON structure exactly
 */
@Data
@Schema(description = "Request data for creating a new article")
public class CreateArticleRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Article title", example = "Abbey Road")
    private String title;

    @NotNull(message = "Artist ID is required")
    @Min(value = 1, message = "Invalid artist ID")
    @Schema(description = "Artist ID", example = "1")
    private Integer artist_id;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have maximum 8 integer digits and 2 decimal places")
    @Schema(description = "Article price", example = "29.99")
    private BigDecimal price;

    @NotNull(message = "Currency ID is required")
    @Min(value = 1, message = "Invalid currency ID")
    @Schema(description = "Currency ID", example = "1")
    private Integer currency_id;

    @NotNull(message = "Music genre ID is required")
    @Min(value = 1, message = "Invalid music genre ID")
    @Schema(description = "Music genre ID", example = "1")
    private Integer music_genre_id;

    @Schema(description = "Release date", example = "1969-09-26", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate release_date;

    @Schema(description = "Article description")
    private String description;

    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    @Schema(description = "Physical dimensions", example = "12 x 12 inches")
    private String dimensions;

    @Min(value = 1, message = "Weight must be positive")
    @Schema(description = "Weight in grams", example = "180")
    private Integer weight_grams;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    @Schema(description = "Barcode", example = "194397215915")
    private String barcode;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Schema(description = "Initial stock quantity", example = "50")
    private Integer stock_quantity;

    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Schema(description = "Minimum stock level", example = "5")
    private Integer min_stock_level = 5;

    @Min(value = 1, message = "Maximum stock level must be positive")
    @Schema(description = "Maximum stock level", example = "100")
    private Integer max_stock_level = 100;

    @Schema(description = "Is article available for purchase", example = "true")
    private Boolean is_available = true;

    @Schema(description = "Is article in preorder", example = "false")
    private Boolean is_preorder = false;

    @Schema(description = "Preorder release date", example = "2024-12-25", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pre_order_release_date;

    @Schema(description = "Preorder end date", example = "2024-12-20", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pre_order_end_date;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Schema(description = "Article image URL", example = "https://example.com/images/abbey-road.jpg")
    private String image_url;

    @NotBlank(message = "Article type is required")
    @Pattern(regexp = "^(vinyl|cassette|cd)$", message = "Type must be vinyl, cassette, or cd")
    @Schema(description = "Article type", example = "vinyl", allowableValues = { "vinyl", "cassette", "cd" })
    private String type;

    @Schema(description = "Type-specific details")
    private Object type_details;
}