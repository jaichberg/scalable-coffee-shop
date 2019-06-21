package sve2.project.orders.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.Value;

import java.util.UUID;

@Value
@JsonDeserialize
public class OrderCoffeeRequest {
  public final UUID orderId;
  public final String beanOrigin;
  public final CoffeeType coffeeType;

  @JsonCreator
  public OrderCoffeeRequest(String beanOrigin, CoffeeType coffeeType) {
    this.orderId = UUID.randomUUID();
    this.beanOrigin = Preconditions.checkNotNull(beanOrigin, "beanOrigin");
    this.coffeeType = Preconditions.checkNotNull(coffeeType, "coffeeType");
  }
}
