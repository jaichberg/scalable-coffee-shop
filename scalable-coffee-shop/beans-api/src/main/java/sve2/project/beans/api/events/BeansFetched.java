package sve2.project.beans.api.events;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;
import sve2.project.beans.api.BeansEvent;

@Value
public class BeansFetched implements BeansEvent, PersistentEntity.ReplyType<Done> {
  private final String beanOrigin;

  @JsonCreator
  public BeansFetched(String beanOrigin) {
    this.beanOrigin = beanOrigin;
  }
}
