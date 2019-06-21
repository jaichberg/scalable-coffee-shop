package sve2.project.orders.impl;

import sve2.project.orders.api.OrderResponse;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface OrdersRepository {
  CompletionStage<Optional<OrderResponse>> getOrder(UUID id);
}
