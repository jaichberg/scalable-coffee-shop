package sve2.project.beans.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.Value;

@Value
@JsonDeserialize
public class StoreBeansRequest {
  public final String beanOrigin;
  public final long amount;

  @JsonCreator
  public StoreBeansRequest(String beanOrigin, long amount) {
    this.beanOrigin = Preconditions.checkNotNull(beanOrigin, "beanOrigin");

    Preconditions.checkArgument(amount != 0, "amount");
    this.amount = amount;
  }
}
