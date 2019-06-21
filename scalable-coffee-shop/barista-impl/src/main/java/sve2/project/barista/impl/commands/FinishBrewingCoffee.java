package sve2.project.barista.impl.commands;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.barista.impl.BaristaCommand;

import java.util.UUID;

@Value
public final class FinishBrewingCoffee implements BaristaCommand, PersistentEntity.ReplyType<Done> {
  private UUID orderId;

  @JsonCreator
  public FinishBrewingCoffee(UUID orderId) {
    this.orderId = orderId;
  }
}

