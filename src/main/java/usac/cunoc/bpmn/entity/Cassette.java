package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Cassette specific entity - 100% compliant with database schema
 */
@Entity
@Table(name = "cassette")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cassette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analog_article_id", nullable = false, unique = true)
    private AnalogArticle analogArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cassette_category_id", nullable = false)
    private CassetteCategory cassetteCategory;

    @Column(length = 100)
    private String brand;

    @Column(name = "is_chrome_tape")
    private Boolean isChromeTape = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}