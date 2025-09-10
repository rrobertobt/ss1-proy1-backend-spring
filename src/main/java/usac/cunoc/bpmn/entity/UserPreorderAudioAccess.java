package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User preorder audio access entity - 100% compliant with database schema
 * Tracks which users have access to which preorder audio files
 */
@Entity
@Table(name = "user_preorder_audio_access", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id",
        "preorder_audio_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreorderAudioAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preorder_audio_id", nullable = false)
    private PreorderAudio preorderAudio;

    @Column(name = "access_granted_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime accessGrantedAt;

    @Column(name = "last_played_at")
    private LocalDateTime lastPlayedAt;

    @Column(name = "play_count")
    private Integer playCount = 0;

    @Column(name = "downloaded")
    private Boolean downloaded = false;

    @Column(name = "downloaded_at")
    private LocalDateTime downloadedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.accessGrantedAt == null) {
            this.accessGrantedAt = now;
        }
        if (this.playCount == null) {
            this.playCount = 0;
        }
        if (this.downloaded == null) {
            this.downloaded = false;
        }
    }
}