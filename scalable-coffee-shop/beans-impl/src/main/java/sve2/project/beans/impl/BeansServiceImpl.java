package sve2.project.beans.impl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Flow;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PSequence;
import sve2.project.beans.api.Bean;
import sve2.project.beans.api.BeansEvent;
import sve2.project.beans.api.BeansService;
import sve2.project.beans.api.StoreBeansRequest;
import sve2.project.beans.impl.commands.ReserveBeans;
import sve2.project.beans.impl.commands.StoreBeans;
import sve2.project.orders.api.Order;
import sve2.project.orders.api.OrdersEvent;
import sve2.project.orders.api.OrdersService;
import sve2.project.orders.api.events.OrderPlaced;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BeansServiceImpl implements BeansService {
  private final PersistentEntityRegistry registry;
  private final BeansRepository repository;

  @Inject
  public BeansServiceImpl(
    PersistentEntityRegistry registry,
    BeansRepository repository,
    OrdersService ordersService) {

    this.registry = registry;
    this.repository = repository;

    ordersService.ordersTopic().subscribe().atLeastOnce(
        Flow.<OrdersEvent>create().mapAsync(1, this::handleOrdersEvent)
    );

    registry.register(BeansEntity.class);
  }

  @Override
  public ServiceCall<NotUsed, PSequence<Bean>> getBeans() {
    return request -> this.repository.getBeans();
  }

  @Override
  public ServiceCall<StoreBeansRequest, Done> storeBeans() {
    return request -> {
      String beanOrigin = request.getBeanOrigin();

      StoreBeans command = new StoreBeans(
        beanOrigin,
        request.getAmount());

      return this.registry
        .refFor(BeansEntity.class, beanOrigin)
        .ask(command);
    };
  }

  @Override
  public Topic<BeansEvent> beansTopic() {
    return TopicProducer.singleStreamWithOffset(
      offset -> this.registry.eventStream(BeansEvent.TAG, offset)
    );
  }

  private CompletionStage<Done> handleOrdersEvent(OrdersEvent event) {
    if (event instanceof OrderPlaced) {
      return handleOrderPlaced((OrderPlaced) event);
    } else {
      return CompletableFuture.completedFuture(Done.getInstance());
    }
  }

  private CompletionStage<Done> handleOrderPlaced(OrderPlaced event) {
    Order order = event.getOrder();

    ReserveBeans command = new ReserveBeans(
      order.getBeanOrigin(),
      order.getOrderId());

    return this.registry
      .refFor(BeansEntity.class, command.getBeanOrigin())
      .ask(command);
  }
}
