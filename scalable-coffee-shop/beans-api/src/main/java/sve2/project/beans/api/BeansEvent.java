package sve2.project.beans.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import sve2.project.beans.api.events.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
  @JsonSubTypes.Type(BeansStored.class),
  @JsonSubTypes.Type(BeansReserved.class),
  @JsonSubTypes.Type(BeansFetched.class),
  @JsonSubTypes.Type(BeansNotAvailable.class),
})
public interface BeansEvent extends AggregateEvent<BeansEvent>, Jsonable {
  AggregateEventTag<BeansEvent> TAG = AggregateEventTag.of(BeansEvent.class);

  @Override
  default AggregateEventTag<BeansEvent> aggregateTag() {
    return TAG;
  }
}
