package sve2.project.orders.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;
import sve2.project.orders.api.Order;
import sve2.project.orders.api.OrderStatus;

@Value
public class OrderState implements Jsonable {
  private final Order order;
  private final OrderStatus status;

  @JsonCreator
  public OrderState(Order order, OrderStatus status) {
    this.order = order;
    this.status = status;
  }

  public OrderState withStatus(OrderStatus status) {
    return new OrderState(this.order, status);
  }
}
