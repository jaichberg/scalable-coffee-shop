package sve2.project.barista.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import sve2.project.barista.api.BaristaEvent;
import sve2.project.barista.api.BaristaService;
import sve2.project.barista.impl.commands.StartBrewingCoffee;
import sve2.project.orders.api.OrdersEvent;
import sve2.project.orders.api.OrdersService;
import sve2.project.orders.api.events.OrderAccepted;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BaristaServiceImpl implements BaristaService {
  private final PersistentEntityRegistry registry;

  @Inject
  public BaristaServiceImpl(
    PersistentEntityRegistry registry,
    OrdersService ordersService) {

    this.registry = registry;

    ordersService.ordersTopic().subscribe().atLeastOnce(
      Flow.<OrdersEvent>create().mapAsync(1, this::handleOrdersEvent)
    );

    registry.register(BaristaEntity.class);
  }

  @Override
  public Topic<BaristaEvent> baristaTopic() {
    return TopicProducer.singleStreamWithOffset(
      offset -> this.registry.eventStream(BaristaEvent.TAG, offset)
    );
  }

  private CompletionStage<Done> handleOrdersEvent(OrdersEvent event) {
    if (event instanceof OrderAccepted) {
      return handleOrderAccepted((OrderAccepted) event);
    } else {
      return CompletableFuture.completedFuture(Done.getInstance());
    }
  }

  private CompletionStage<Done> handleOrderAccepted(OrderAccepted event) {
    StartBrewingCoffee command = new StartBrewingCoffee(
      event.getOrderId());

    return this.registry
      .refFor(BaristaEntity.class, command.getOrderId().toString())
      .ask(command);
  }
}
