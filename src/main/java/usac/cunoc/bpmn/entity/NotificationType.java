package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Notification type catalog entity - 100% compliant with database schema
 * Represents system notification types like "Preventa Disponible", "Evento
 * Proximo", etc.
 */
@Entity
@Table(name = "notification_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_email")
    private Boolean isEmail = true;

    @Column(name = "is_system")
    private Boolean isSystem = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isEmail == null) {
            this.isEmail = true;
        }
        if (this.isSystem == null) {
            this.isSystem = true;
        }
    }
}