package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Vinyl specific entity - 100% compliant with database schema
 */
@Entity
@Table(name = "vinyl")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vinyl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analog_article_id", nullable = false, unique = true)
    private AnalogArticle analogArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinyl_category_id", nullable = false)
    private VinylCategory vinylCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinyl_special_edition_id")
    private VinylSpecialEdition vinylSpecialEdition;

    @Column(columnDefinition = "INTEGER DEFAULT 33 CHECK (rpm IN (33, 45))")
    private Integer rpm = 33;

    @Column(name = "is_limited_edition")
    private Boolean isLimitedEdition = false;

    @Column(name = "remaining_limited_stock")
    private Integer remainingLimitedStock;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}