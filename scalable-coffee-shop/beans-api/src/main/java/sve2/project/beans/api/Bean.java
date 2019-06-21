package sve2.project.beans.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public final class Bean implements Jsonable {
  public final String beanOrigin;
  public final long amount;

  @JsonCreator
  public Bean(String beanOrigin, long amount) {
    this.beanOrigin = beanOrigin;
    this.amount = amount;
  }
}
