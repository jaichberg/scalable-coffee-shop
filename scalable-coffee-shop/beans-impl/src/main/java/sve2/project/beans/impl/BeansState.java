package sve2.project.beans.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
@JsonDeserialize
public final class BeansState implements Jsonable {
  public String origin;
  public long amount;

  @JsonCreator
  public BeansState(String origin, long amount) {
    this.origin = origin;
    this.amount = amount;
  }

  public BeansState addAmount(long amount) {
    return new BeansState(this.origin, this.amount + amount);
  }
}
