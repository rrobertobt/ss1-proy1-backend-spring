package usac.cunoc.bpmn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order entity - 100% compliant with database schema
 */
@Entity
@Table(name = "\"order\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal discountAmount;

    @Column(name = "shipping_cost", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal shippingCost;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_items", columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private UserAddress shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id", nullable = false)
    private UserAddress billingAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.taxAmount == null) {
            this.taxAmount = BigDecimal.ZERO;
        }
        if (this.discountAmount == null) {
            this.discountAmount = BigDecimal.ZERO;
        }
        if (this.shippingCost == null) {
            this.shippingCost = BigDecimal.ZERO;
        }
        if (this.totalItems == null) {
            this.totalItems = 0;
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