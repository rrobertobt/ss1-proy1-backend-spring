package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Preorder audio entity - 100% compliant with database schema
 * Represents audio files for preorder articles preview
 */
@Entity
@Table(name = "preorder_audio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreorderAudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analog_article_id", nullable = false)
    private AnalogArticle analogArticle;

    @Column(name = "audio_file_url", nullable = false, length = 500)
    private String audioFileUrl;

    @Column(name = "track_title", length = 255)
    private String trackTitle;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "is_downloadable")
    private Boolean isDownloadable = true;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.downloadCount == null) {
            this.downloadCount = 0;
        }
        if (this.isDownloadable == null) {
            this.isDownloadable = true;
        }
    }
}