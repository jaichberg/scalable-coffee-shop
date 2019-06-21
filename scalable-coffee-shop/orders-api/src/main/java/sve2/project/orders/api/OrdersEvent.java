package sve2.project.orders.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import sve2.project.orders.api.events.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
  @JsonSubTypes.Type(OrderPlaced.class),
  @JsonSubTypes.Type(OrderAccepted.class),
  @JsonSubTypes.Type(OrderStarted.class),
  @JsonSubTypes.Type(OrderFinished.class),
  @JsonSubTypes.Type(OrderDelivered.class),
  @JsonSubTypes.Type(OrderCancelled.class),
})
public interface OrdersEvent extends AggregateEvent<OrdersEvent>, Jsonable{
  AggregateEventTag<OrdersEvent> TAG = AggregateEventTag.of(OrdersEvent.class);

  @Override
  default AggregateEventTagger<OrdersEvent> aggregateTag() {
    return TAG;
  }
}
