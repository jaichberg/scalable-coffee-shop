package sve2.project.beans.impl.commands;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.beans.impl.BeansCommand;

import java.util.UUID;

@Value
public final class ReserveBeans implements BeansCommand, PersistentEntity.ReplyType<Done> {
  private String beanOrigin;
  private UUID orderId;

  @JsonCreator
  public ReserveBeans(String beanOrigin, UUID orderId) {
    this.beanOrigin = beanOrigin;
    this.orderId = orderId;
  }
}

