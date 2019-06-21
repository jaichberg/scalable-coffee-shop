package sve2.project.orders.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;
import sve2.project.orders.api.OrdersEvent;

import java.util.UUID;

@Value
public class OrderStarted implements OrdersEvent {
  private final UUID orderId;

  @JsonCreator
  public OrderStarted(UUID orderId) {
    this.orderId = orderId;
  }
}
