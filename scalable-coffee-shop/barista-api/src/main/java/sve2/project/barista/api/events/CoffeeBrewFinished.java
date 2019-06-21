package sve2.project.barista.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;
import sve2.project.barista.api.BaristaEvent;

import java.util.UUID;

@Value
public class CoffeeBrewFinished implements BaristaEvent {
  private final UUID orderId;

  @JsonCreator
  public CoffeeBrewFinished(UUID orderId) {
    this.orderId = orderId;
  }
}
