package sve2.project.orders.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;

import java.util.Optional;
import java.util.UUID;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface OrdersService extends Service {
  String ORDERS_TOPIC = "orders";

  @Override
  default Descriptor descriptor() {
    return named("orders")
      .withCalls(
        pathCall("/orders/:id", this::getOrder),
        namedCall("/orders", this::orderCoffee)
      )
      .withTopics(topic(ORDERS_TOPIC, this::ordersTopic));
  }

  ServiceCall<NotUsed, Optional<OrderResponse>> getOrder(UUID id);

  ServiceCall<OrderCoffeeRequest, Done> orderCoffee();

  Topic<OrdersEvent> ordersTopic();
}
