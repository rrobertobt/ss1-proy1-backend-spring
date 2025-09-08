package usac.cunoc.bpmn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Composite key for EventRegistration entity
 * Based on database composite primary key (event_id, user_id)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationId implements Serializable {

    private Integer event;
    private Integer user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventRegistrationId)) return false;
        EventRegistrationId that = (EventRegistrationId) o;
        return event != null && event.equals(that.event) &&
               user != null && user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}