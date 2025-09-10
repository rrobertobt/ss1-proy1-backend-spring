package usac.cunoc.bpmn.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Composite key for CD promotion article junction table
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdPromotionArticleId implements Serializable {

    private Integer cdPromotionId;
    private Integer analogArticleId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CdPromotionArticleId))
            return false;
        CdPromotionArticleId that = (CdPromotionArticleId) o;
        return cdPromotionId.equals(that.cdPromotionId) &&
                analogArticleId.equals(that.analogArticleId);
    }

    @Override
    public int hashCode() {
        return cdPromotionId.hashCode() + analogArticleId.hashCode();
    }
}