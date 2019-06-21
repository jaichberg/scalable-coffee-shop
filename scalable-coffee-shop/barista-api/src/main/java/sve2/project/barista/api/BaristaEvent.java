package sve2.project.barista.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import sve2.project.barista.api.events.CoffeeBrewFinished;
import sve2.project.barista.api.events.CoffeeBrewStarted;
import sve2.project.barista.api.events.CoffeeDelivered;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
  @JsonSubTypes.Type(CoffeeBrewStarted.class),
  @JsonSubTypes.Type(CoffeeBrewFinished.class),
  @JsonSubTypes.Type(CoffeeDelivered.class),
})
public interface BaristaEvent extends AggregateEvent<BaristaEvent>, Jsonable {
  AggregateEventTag<BaristaEvent> TAG = AggregateEventTag.of(BaristaEvent.class);

  @Override
  default AggregateEventTagger<BaristaEvent> aggregateTag() {
    return TAG;
  }
}
