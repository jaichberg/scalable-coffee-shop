package sve2.project.orders.impl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Flow;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import sve2.project.barista.api.BaristaEvent;
import sve2.project.barista.api.BaristaService;
import sve2.project.barista.api.events.CoffeeBrewFinished;
import sve2.project.barista.api.events.CoffeeDelivered;
import sve2.project.beans.api.BeansEvent;
import sve2.project.beans.api.BeansService;
import sve2.project.beans.api.events.BeansNotAvailable;
import sve2.project.beans.api.events.BeansReserved;
import sve2.project.orders.api.*;
import sve2.project.orders.impl.commands.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OrdersServiceImpl implements OrdersService {
  private final PersistentEntityRegistry registry;
  private final OrdersRepository repository;

  @Inject
  public OrdersServiceImpl(
    PersistentEntityRegistry registry,
    OrdersRepository repository,
    BeansService beansService,
    BaristaService baristaService) {

    this.registry = registry;
    this.repository = repository;

    beansService.beansTopic().subscribe().atLeastOnce(
      Flow.<BeansEvent>create().mapAsync(1, this::handleBeansEvent)
    );

    baristaService.baristaTopic().subscribe().atLeastOnce(
      Flow.<BaristaEvent>create().mapAsync(1, this::handleBaristaEvent)
    );

    registry.register(OrderEntity.class);
  }

  @Override
  public ServiceCall<OrderCoffeeRequest, Done> orderCoffee() {
    return request -> {
      UUID orderId = request.getOrderId();

      Order order = new Order(
        orderId,
        request.getCoffeeType(),
        request.getBeanOrigin());

      PlaceOrder command = new PlaceOrder(order);

      return this.registry
        .refFor(OrderEntity.class, orderId.toString())
        .ask(command);
    };
  }

  @Override
  public ServiceCall<NotUsed, Optional<OrderResponse>> getOrder(UUID id) {
    return request -> this.repository.getOrder(id);
  }

  @Override
  public Topic<OrdersEvent> ordersTopic() {
    return TopicProducer.singleStreamWithOffset(
      offset -> this.registry.eventStream(OrdersEvent.TAG, offset)
    );
  }

  private CompletionStage<Done> handleBeansEvent(BeansEvent event) {
    if (event instanceof BeansReserved) {
      return handleBeansReserved((BeansReserved) event);
    } else if (event instanceof BeansNotAvailable) {
      return handleBeansNotAvailable((BeansNotAvailable) event);
    } else {
      return CompletableFuture.completedFuture(Done.getInstance());
    }
  }

  private CompletionStage<Done> handleBeansReserved(BeansReserved event) {
    AcceptOrder command = new AcceptOrder(
      event.getOrderId());

    return this.registry
      .refFor(OrderEntity.class, command.getOrderId().toString())
      .ask(command);
  }

  private CompletionStage<Done> handleBeansNotAvailable(BeansNotAvailable event) {
    CancelOrder command = new CancelOrder(
      event.getOrderId(),
      "No beans of the origin were available");

    return this.registry
      .refFor(OrderEntity.class, command.getOrderId().toString())
      .ask(command);
  }

  private CompletionStage<Done> handleBaristaEvent(BaristaEvent event) {
    if (event instanceof CoffeeBrewFinished) {
      return handleCoffeeBrewFinished((CoffeeBrewFinished) event);
    } else if (event instanceof CoffeeDelivered) {
      return handleCoffeeDelivered((CoffeeDelivered) event);
    } else {
      return CompletableFuture.completedFuture(Done.getInstance());
    }
  }

  private CompletionStage<Done> handleCoffeeBrewFinished(CoffeeBrewFinished event) {
    FinishOrder command = new FinishOrder(
      event.getOrderId());

    return this.registry
      .refFor(OrderEntity.class, command.getOrderId().toString())
      .ask(command);
  }

  private CompletionStage<Done> handleCoffeeDelivered(CoffeeDelivered event) {
    DeliverOrder command = new DeliverOrder(
      event.getOrderId());

    return this.registry
      .refFor(OrderEntity.class, command.getOrderId().toString())
      .ask(command);
  }
}
