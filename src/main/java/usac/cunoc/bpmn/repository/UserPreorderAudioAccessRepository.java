package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.UserPreorderAudioAccess;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserPreorderAudioAccess entity operations
 */
@Repository
public interface UserPreorderAudioAccessRepository extends JpaRepository<UserPreorderAudioAccess, Integer> {

    /**
     * Find user's accessible audios with article and artist details
     */
    @Query("SELECT upaa FROM UserPreorderAudioAccess upaa " +
            "LEFT JOIN FETCH upaa.preorderAudio pa " +
            "LEFT JOIN FETCH pa.analogArticle aa " +
            "LEFT JOIN FETCH aa.artist " +
            "WHERE upaa.user.id = :userId " +
            "ORDER BY upaa.accessGrantedAt DESC")
    List<UserPreorderAudioAccess> findAccessibleAudiosByUserId(@Param("userId") Integer userId);

    /**
     * Find specific user audio access
     */
    @Query("SELECT upaa FROM UserPreorderAudioAccess upaa " +
            "LEFT JOIN FETCH upaa.preorderAudio pa " +
            "LEFT JOIN FETCH pa.analogArticle aa " +
            "LEFT JOIN FETCH aa.artist " +
            "WHERE upaa.user.id = :userId " +
            "AND upaa.preorderAudio.id = :audioId")
    Optional<UserPreorderAudioAccess> findByUserIdAndAudioId(@Param("userId") Integer userId,
            @Param("audioId") Integer audioId);

    /**
     * Check if user has access to specific audio
     */
    boolean existsByUserIdAndPreorderAudioId(Integer userId, Integer preorderAudioId);

    /**
     * Update play statistics
     */
    @Modifying
    @Query("UPDATE UserPreorderAudioAccess upaa " +
            "SET upaa.playCount = upaa.playCount + 1, " +
            "upaa.lastPlayedAt = :playTime " +
            "WHERE upaa.id = :accessId")
    void updatePlayStatistics(@Param("accessId") Integer accessId, @Param("playTime") LocalDateTime playTime);

    /**
     * Mark as downloaded
     */
    @Modifying
    @Query("UPDATE UserPreorderAudioAccess upaa " +
            "SET upaa.downloaded = true, " +
            "upaa.downloadedAt = :downloadTime " +
            "WHERE upaa.id = :accessId")
    void markAsDownloaded(@Param("accessId") Integer accessId, @Param("downloadTime") LocalDateTime downloadTime);

    /**
     * Count total accessible audios for user
     */
    @Query("SELECT COUNT(upaa) FROM UserPreorderAudioAccess upaa WHERE upaa.user.id = :userId")
    Integer countAccessibleAudiosByUserId(@Param("userId") Integer userId);
}