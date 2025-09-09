package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * WishlistItem entity - 100% compliant with database schema
 * Represents individual items in user's wishlist with preorder payment tracking
 */
@Entity
@Table(name = "wishlist_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analog_article_id", nullable = false)
    private AnalogArticle analogArticle;

    @Column(name = "is_preorder_paid")
    private Boolean isPreorderPaid = false;

    @Column(name = "notification_sent")
    private Boolean notificationSent = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        // WishlistItem doesn't have updated_at column per schema
    }
}