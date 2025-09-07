package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User entity - 100% compliant with database schema
 */
@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // Defaults exactos según esquema BD
    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive;

    @Column(name = "is_verified", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isVerified;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "two_factor_code", length = 6)
    private String twoFactorCode;

    @Column(name = "two_factor_code_expires")
    private LocalDateTime twoFactorCodeExpires;

    @Column(name = "failed_login_attempts", columnDefinition = "INTEGER DEFAULT 0")
    private Integer failedLoginAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "is_2fa_enabled", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean is2faEnabled;

    @Column(name = "deleted_comments_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer deletedCommentsCount;

    @Column(name = "is_banned", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isBanned;

    @Column(name = "total_spent", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal totalSpent;

    @Column(name = "total_orders", columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalOrders;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        // Set defaults exactos según BD schema si son null
        if (this.isActive == null)
            this.isActive = true;
        if (this.isVerified == null)
            this.isVerified = false;
        if (this.failedLoginAttempts == null)
            this.failedLoginAttempts = 0;
        if (this.is2faEnabled == null)
            this.is2faEnabled = false;
        if (this.deletedCommentsCount == null)
            this.deletedCommentsCount = 0;
        if (this.isBanned == null)
            this.isBanned = false;
        if (this.totalSpent == null)
            this.totalSpent = BigDecimal.ZERO;
        if (this.totalOrders == null)
            this.totalOrders = 0;
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