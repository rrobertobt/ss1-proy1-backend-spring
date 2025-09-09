package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity - 100% compliant with database schema
 */
@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payment_number", unique = true, nullable = false, length = 50)
    private String paymentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_status_id", nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_reference", length = 255)
    private String transactionReference;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "gateway_transaction_id", length = 255)
    private String gatewayTransactionId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "refunded_amount", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal refundedAmount;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.refundedAmount == null) {
            this.refundedAmount = BigDecimal.ZERO;
        }
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}