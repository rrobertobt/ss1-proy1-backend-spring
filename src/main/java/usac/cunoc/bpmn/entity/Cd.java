package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * CD specific entity - 100% compliant with database schema
 */
@Entity
@Table(name = "cd")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analog_article_id", nullable = false, unique = true)
    private AnalogArticle analogArticle;

    @Column(name = "disc_count", columnDefinition = "INTEGER DEFAULT 1 CHECK (disc_count > 0)")
    private Integer discCount = 1;

    @Column(name = "has_bonus_content")
    private Boolean hasBonusContent = false;

    @Column(name = "is_remastered")
    private Boolean isRemastered = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}