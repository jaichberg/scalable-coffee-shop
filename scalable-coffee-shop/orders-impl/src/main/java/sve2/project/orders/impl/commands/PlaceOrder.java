package sve2.project.orders.impl.commands;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.orders.api.Order;
import sve2.project.orders.impl.OrderCommand;

@Value
public final class PlaceOrder implements OrderCommand, PersistentEntity.ReplyType<Done> {
  private Order order;

  @JsonCreator
  public PlaceOrder(Order order) {
    this.order = order;
  }
}


