package sve2.project.orders.impl;

import com.datastax.driver.core.Row;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import sve2.project.orders.api.CoffeeType;
import sve2.project.orders.api.OrderResponse;
import sve2.project.orders.api.OrderStatus;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class OrdersRepositoryImpl implements OrdersRepository {

  private static final String SELECT_ORDER = "SELECT * FROM orders WHERE id = ?";

  private final CassandraSession session;

  @Inject
  public OrdersRepositoryImpl(CassandraSession session, ReadSide readSide) {
    this.session = session;

    readSide.register(OrdersEventProcessor.class);
  }

  @Override
  public CompletionStage<Optional<OrderResponse>> getOrder(UUID id) {
    return session
      .selectOne(SELECT_ORDER, id)
      .thenApply(row -> row.map(this::mapOrder));
  }

  private OrderResponse mapOrder(Row row) {
    return new OrderResponse(
      row.getUUID("orderId"),
      row.get("coffeeType", CoffeeType.class),
      row.getString("beanOrigin"),
      row.get("orderStatus", OrderStatus.class));
  }
}
