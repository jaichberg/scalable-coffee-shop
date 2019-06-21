package sve2.project.orders.impl.commands;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.orders.impl.OrderCommand;

import java.util.UUID;

@Value
public final class AcceptOrder implements OrderCommand, PersistentEntity.ReplyType<Done> {
  private UUID orderId;

  @JsonCreator
  public AcceptOrder(UUID orderId) {
    this.orderId = orderId;
  }
}

