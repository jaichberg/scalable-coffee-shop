package sve2.project.orders.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;
import sve2.project.orders.api.OrdersEvent;

import java.util.UUID;

@Value
public class OrderCancelled implements OrdersEvent {
  private final UUID orderId;
  private final String reason;

  @JsonCreator
  public OrderCancelled(UUID orderId, String reason) {
    this.orderId = orderId;
    this.reason = reason;
  }
}
