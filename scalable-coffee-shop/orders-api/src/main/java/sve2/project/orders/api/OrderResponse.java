package sve2.project.orders.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

import java.util.UUID;

@Value
@JsonDeserialize
public class OrderResponse {
  private final UUID orderId;
  private final CoffeeType coffeeType;
  private final String beanOrigin;
  private final OrderStatus orderStatus;

  @JsonCreator
  public OrderResponse(
    UUID orderId,
    CoffeeType coffeeType,
    String beanOrigin,
    OrderStatus orderStatus) {

    this.orderId = orderId;
    this.coffeeType = coffeeType;
    this.beanOrigin = beanOrigin;
    this.orderStatus = orderStatus;
  }
}

