package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Vinyl special edition catalog entity - 100% compliant with database schema
 */
@Entity
@Table(name = "vinyl_special_edition")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VinylSpecialEdition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String color;

    @Column(name = "material_description", columnDefinition = "TEXT")
    private String materialDescription;

    @Column(name = "extra_content", columnDefinition = "TEXT")
    private String extraContent;

    @Column(name = "is_limited")
    private Boolean isLimited = true;

    @Column(name = "limited_quantity")
    private Integer limitedQuantity;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}