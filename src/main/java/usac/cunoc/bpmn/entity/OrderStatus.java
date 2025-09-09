package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Order status entity - 100% compliant with database schema
 */
@Entity
@Table(name = "order_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_final_status", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isFinalStatus;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.isFinalStatus == null) {
            this.isFinalStatus = false;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}