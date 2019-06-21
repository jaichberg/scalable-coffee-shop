package sve2.project.barista.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;
import sve2.project.barista.api.BaristaEvent;

import java.util.UUID;

@Value
public class CoffeeDelivered implements BaristaEvent {
  private final UUID orderId;

  @JsonCreator
  public CoffeeDelivered(UUID orderId) {
    this.orderId = orderId;
  }
}
