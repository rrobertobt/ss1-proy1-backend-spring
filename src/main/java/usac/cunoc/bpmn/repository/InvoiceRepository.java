package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Invoice;
import java.util.Optional;

/**
 * Repository interface for Invoice entity operations
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    /**
     * Find invoice by order ID
     */
    Optional<Invoice> findByOrderId(Integer orderId);

    /**
     * Find invoice by invoice number
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Check if invoice number exists
     */
    boolean existsByInvoiceNumber(String invoiceNumber);

    /**
     * Check if invoice exists for order
     */
    boolean existsByOrderId(Integer orderId);
}