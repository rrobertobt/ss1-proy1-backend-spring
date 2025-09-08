package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Event registration entity - 100% compliant with database schema
 * Composite primary key (event_id, user_id)
 */
@Entity
@Table(name = "event_registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(EventRegistrationId.class)
public class EventRegistration {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "registered_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registeredAt;

    @Column(name = "attended")
    private Boolean attended = false;

    @Column(name = "attendance_duration_seconds")
    private Integer attendanceDurationSeconds = 0;

    @PrePersist
    public void prePersist() {
        if (this.registeredAt == null) {
            this.registeredAt = LocalDateTime.now();
        }
        if (this.attended == null) {
            this.attended = false;
        }
        if (this.attendanceDurationSeconds == null) {
            this.attendanceDurationSeconds = 0;
        }
    }
}