package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Analog article entity - 100% compliant with database schema
 */
@Entity
@Table(name = "analog_article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalogArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_genre_id", nullable = false)
    private MusicGenre musicGenre;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String dimensions;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(length = 50)
    private String barcode;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "min_stock_level")
    private Integer minStockLevel = 5;

    @Column(name = "max_stock_level")
    private Integer maxStockLevel = 100;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_preorder")
    private Boolean isPreorder = false;

    @Column(name = "preorder_release_date")
    private LocalDate preorderReleaseDate;

    @Column(name = "preorder_end_date")
    private LocalDate preorderEndDate;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "total_sold")
    private Integer totalSold = 0;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get article type based on associated tables
     */
    @Transient
    public String getArticleType() {
        // This will be set by service layer based on vinyl/cassette/cd relationships
        return "unknown";
    }
}