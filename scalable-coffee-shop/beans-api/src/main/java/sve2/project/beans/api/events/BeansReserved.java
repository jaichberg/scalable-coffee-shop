package sve2.project.beans.api.events;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.beans.api.BeansEvent;

import java.util.UUID;

@Value
public class BeansReserved implements BeansEvent, PersistentEntity.ReplyType<Done> {
  private final UUID orderId;

  @JsonCreator
  public BeansReserved(UUID orderId) {
    this.orderId = orderId;
  }
}
