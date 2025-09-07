package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Vinyl category catalog entity (size) - 100% compliant with database schema
 */
@Entity
@Table(name = "vinyl_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VinylCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 20)
    private String size;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "typical_rpm")
    private Integer typicalRpm = 33;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}