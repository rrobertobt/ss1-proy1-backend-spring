package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * CD promotion article junction entity - 100% compliant with database schema
 */
@Entity
@Table(name = "cd_promotion_article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdPromotionArticle {

    @EmbeddedId
    private CdPromotionArticleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cdPromotionId")
    @JoinColumn(name = "cd_promotion_id", nullable = false)
    private CdPromotion cdPromotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("analogArticleId")
    @JoinColumn(name = "analog_article_id", nullable = false)
    private AnalogArticle analogArticle;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}