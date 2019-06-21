package sve2.project.orders.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;
import sve2.project.orders.api.OrdersEvent;

import java.util.UUID;

@Value
public class OrderFinished implements OrdersEvent {
  private final UUID orderId;

  @JsonCreator
  public OrderFinished(UUID orderId) {
    this.orderId = orderId;
  }
}
