package sve2.project.barista.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;

@Value
public class BaristaState implements Jsonable {
  private final UUID orderId;
  private final BaristaStatus status;

  @JsonCreator
  public BaristaState(UUID orderId, BaristaStatus status) {
    this.orderId = orderId;
    this.status = status;
  }

  public BaristaState withStatus(BaristaStatus status) {
    return new BaristaState(this.orderId, status);
  }
}
