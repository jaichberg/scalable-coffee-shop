package sve2.project.orders.impl;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.extras.codecs.enums.EnumNameCodec;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import sve2.project.orders.api.CoffeeType;
import sve2.project.orders.api.Order;
import sve2.project.orders.api.OrderStatus;
import sve2.project.orders.api.OrdersEvent;
import sve2.project.orders.api.events.*;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatements;

public class OrdersEventProcessor extends ReadSideProcessor<OrdersEvent> {
  private final CassandraSession session;
  private final CassandraReadSide readSide;

  private PreparedStatement insertOrderStatement;
  private PreparedStatement updateOrderStatusStatement;

  @Inject
  public OrdersEventProcessor(CassandraSession session, CassandraReadSide readSide) {
    this.session = session;
    this.readSide = readSide;
  }

  @Override
  public PSequence<AggregateEventTag<OrdersEvent>> aggregateTags() {
    return TreePVector.singleton(OrdersEvent.TAG);
  }

  @Override
  public ReadSideHandler<OrdersEvent> buildHandler() {
    return readSide.<OrdersEvent>builder("orders_offset")
      .setGlobalPrepare(this::createTables)
      .setPrepare(tag -> this.prepareStatements())
      .setEventHandler(OrderPlaced.class, this::processOrderPlaced)
      .setEventHandler(OrderAccepted.class, this::processOrderAccepted)
      .setEventHandler(OrderStarted.class, this::processOrderStarted)
      .setEventHandler(OrderFinished.class, this::processOrderFinished)
      .setEventHandler(OrderDelivered.class, this::processOrderDelivered)
      .setEventHandler(OrderCancelled.class, this::processOrderCancelled)
      .build();
  }

  private CompletionStage<Done> createTables() {
    return session.executeCreateTable(
      "CREATE TABLE IF NOT EXISTS orders(" +
        "orderId UUID," +
        "coffeeType TEXT," +
        "beanOrigin TEXT," +
        "orderStatus TEXT," +
        "PRIMARY KEY (orderId)" +
      ")"
    );
  }

  private CompletionStage<Done> prepareStatements() {
    return CompletableFuture.allOf(
      registerCoffeeTypeCodec().toCompletableFuture(),
      registerOrderStatusCodec().toCompletableFuture(),
      prepareInsertOrderStatement().toCompletableFuture(),
      prepareUpdateOrderStatusStatement().toCompletableFuture()
    ).thenApply(ignoredVoid -> Done.getInstance());
  }

  private CompletionStage<Done> registerCoffeeTypeCodec() {
    return this.session.underlying()
      .thenAccept(session -> registerCodec(session, new EnumNameCodec<>(CoffeeType.class)))
      .thenApply(ignoredVoid -> Done.getInstance());
  }

  private CompletionStage<Done> registerOrderStatusCodec() {
    return this.session.underlying()
      .thenAccept(session -> registerCodec(session, new EnumNameCodec<>(OrderStatus.class)))
      .thenApply(ignoredVoid -> Done.getInstance());
  }

  private void registerCodec(Session session, TypeCodec<?> codec) {
    session
      .getCluster()
      .getConfiguration()
      .getCodecRegistry()
      .register(codec);
  }

  private CompletionStage<Done> prepareInsertOrderStatement() {
    return this.session
      .prepare("INSERT INTO orders(orderId, coffeeType, beanOrigin, orderStatus) VALUES (?, ?, ?, ?)")
      .thenApply(statement -> {
        this.insertOrderStatement = statement;
        return Done.getInstance();
      });
  }

  private CompletionStage<Done> prepareUpdateOrderStatusStatement() {
    return this.session
      .prepare("UPDATE orders SET orderStatus = ? WHERE orderId = ?")
      .thenApply(statement -> {
        this.updateOrderStatusStatement = statement;
        return Done.getInstance();
      });
  }

  private CompletionStage<List<BoundStatement>> processOrderPlaced(OrderPlaced event) {
    Order order = event.getOrder();

    return completedStatements(this.insertOrderStatement.bind(
      order.getOrderId(),
      order.getCoffeeType(),
      order.getBeanOrigin(),
      OrderStatus.PLACED)
    );
  }

  private CompletionStage<List<BoundStatement>> processOrderAccepted(OrderAccepted event) {
    return completedStatements(this.updateOrderStatusStatement.bind(
      OrderStatus.ACCEPTED,
      event.getOrderId())
    );
  }

  private CompletionStage<List<BoundStatement>> processOrderStarted(OrderStarted event) {
    return completedStatements(this.updateOrderStatusStatement.bind(
      OrderStatus.STARTED,
      event.getOrderId())
    );
  }

  private CompletionStage<List<BoundStatement>> processOrderFinished(OrderFinished event) {
    return completedStatements(this.updateOrderStatusStatement.bind(
      OrderStatus.FINISHED,
      event.getOrderId())
    );
  }

  private CompletionStage<List<BoundStatement>> processOrderDelivered(OrderDelivered event) {
    return completedStatements(this.updateOrderStatusStatement.bind(
      OrderStatus.DELIVERED,
      event.getOrderId())
    );
  }

  private CompletionStage<List<BoundStatement>> processOrderCancelled(OrderCancelled event) {
    return completedStatements(this.updateOrderStatusStatement.bind(
      OrderStatus.CANCELLED,
      event.getOrderId())
    );
  }
}
