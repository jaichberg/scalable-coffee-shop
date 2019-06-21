package sve2.project.beans.impl.commands;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.beans.impl.BeansCommand;

@Value
public final class StoreBeans implements BeansCommand, PersistentEntity.ReplyType<Done> {
  private String beanOrigin;
  private long amount;

  @JsonCreator
  public StoreBeans(String beanOrigin, long amount) {
    this.beanOrigin = beanOrigin;
    this.amount = amount;
  }
}

