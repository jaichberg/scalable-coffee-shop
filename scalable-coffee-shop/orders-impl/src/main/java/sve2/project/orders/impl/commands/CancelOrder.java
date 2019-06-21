package sve2.project.orders.impl.commands;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.orders.impl.OrderCommand;

import java.util.UUID;

@Value
public final class CancelOrder implements OrderCommand, PersistentEntity.ReplyType<Done> {
  private UUID orderId;
  private String reason;

  @JsonCreator
  public CancelOrder(UUID orderId, String reason) {
    this.orderId = orderId;
    this.reason = reason;
  }
}

