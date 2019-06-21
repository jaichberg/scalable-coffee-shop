package sve2.project.orders.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;
import sve2.project.orders.api.Order;
import sve2.project.orders.api.OrdersEvent;

@Value
public class OrderPlaced implements OrdersEvent {
  private final Order order;

  @JsonCreator
  public OrderPlaced(Order order) {
    this.order = order;
  }
}
