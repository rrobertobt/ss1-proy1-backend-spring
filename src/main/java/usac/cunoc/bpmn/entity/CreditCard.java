package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Credit card entity with encrypted data - exact database compliance
 */
@Entity
@Table(name = "credit_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_number_encrypted", nullable = false, columnDefinition = "TEXT")
    private String cardNumberEncrypted;

    @Column(name = "cardholder_name_encrypted", nullable = false, columnDefinition = "TEXT")
    private String cardholderNameEncrypted;

    @Column(name = "expiry_month_encrypted", nullable = false, columnDefinition = "TEXT")
    private String expiryMonthEncrypted;

    @Column(name = "expiry_year_encrypted", nullable = false, columnDefinition = "TEXT")
    private String expiryYearEncrypted;

    @Column(name = "cvv_encrypted", nullable = false, columnDefinition = "TEXT")
    private String cvvEncrypted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_brand_id", nullable = false)
    private CardBrand cardBrand;

    @Column(name = "last_four_digits", nullable = false, length = 4)
    private String lastFourDigits;

    @Column(name = "is_default", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDefault;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.isDefault == null)
            this.isDefault = false;
        if (this.isActive == null)
            this.isActive = true;
        if (this.createdAt == null)
            this.createdAt = now;
        if (this.updatedAt == null)
            this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}