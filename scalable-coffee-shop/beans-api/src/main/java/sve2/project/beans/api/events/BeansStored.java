package sve2.project.beans.api.events;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.beans.api.BeansEvent;

@Value
public class BeansStored implements BeansEvent, PersistentEntity.ReplyType<Done> {
  private final String beanOrigin;
  private final long amount;

  @JsonCreator
  public BeansStored(String beanOrigin, long amount) {
    this.beanOrigin = beanOrigin;
    this.amount = amount;
  }
}
