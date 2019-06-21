package sve2.project.orders.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import sve2.project.orders.api.OrdersEvent;
import sve2.project.orders.api.events.*;
import sve2.project.orders.impl.commands.*;

import java.util.Optional;
import java.util.function.Function;

import static sve2.project.orders.api.OrderStatus.*;

public class OrderEntity extends PersistentEntity<OrderCommand, OrdersEvent, Optional<OrderState>> {

  @Override
  public Behavior initialBehavior(Optional<Optional<OrderState>> snapshotState) {
    Optional<OrderState> state = snapshotState.flatMap(Function.identity());

    if (!state.isPresent()) {
      return notCreated();
    } else {
      switch (state.get().getStatus()) {
        case PLACED:
          return placed(state.get());
        case ACCEPTED:
          return accepted(state.get());
        case STARTED:
          return started(state.get());
        case FINISHED:
          return finished(state.get());
        case DELIVERED:
          return delivered(state.get());
        case CANCELLED:
          return cancelled(state.get());
        default:
          throw new IllegalStateException();
      }
    }
  }

  private Behavior notCreated() {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.empty());

    // Command Handlers:
    builder.setCommandHandler(PlaceOrder.class,
      (cmd, ctx) -> ctx.thenPersist(
        new OrderPlaced(cmd.getOrder())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(OrderPlaced.class, event ->
      placed(new OrderState(event.getOrder(), PLACED)));

    return builder.build();
  }

  private Behavior placed(OrderState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(PlaceOrder.class, this::alreadyDone);

    builder.setCommandHandler(AcceptOrder.class,
      (cmd, ctx) -> ctx.thenPersist(
        new OrderAccepted(cmd.getOrderId())));

    builder.setCommandHandler(CancelOrder.class,
      (cmd, ctx) -> ctx.thenPersist(
        new OrderCancelled(cmd.getOrderId(), cmd.getReason())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(OrderAccepted.class, event ->
      accepted(state.withStatus(ACCEPTED)));

    builder.setEventHandlerChangingBehavior(OrderCancelled.class, event ->
      cancelled(state.withStatus(CANCELLED)));

    return builder.build();
  }

  private Behavior accepted(OrderState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(PlaceOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(AcceptOrder.class, this::alreadyDone);

    builder.setCommandHandler(StartOrder.class,
      (cmd, ctx) -> ctx.thenPersist(
        new OrderStarted(cmd.getOrderId())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(OrderStarted.class, event ->
      started(state.withStatus(STARTED)));

    return builder.build();
  }

  private Behavior started(OrderState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(PlaceOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(AcceptOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(StartOrder.class, this::alreadyDone);

    builder.setCommandHandler(FinishOrder.class,
      (cmd, ctx) -> ctx.thenPersist(
        new OrderFinished(cmd.getOrderId())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(OrderFinished.class, event ->
      finished(state.withStatus(FINISHED)));

    return builder.build();
  }

  private Behavior finished(OrderState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(PlaceOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(AcceptOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(StartOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(FinishOrder.class, this::alreadyDone);

    builder.setCommandHandler(DeliverOrder.class,
      (cmd, ctx) -> ctx.thenPersist(
        new OrderDelivered(cmd.getOrderId())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(OrderDelivered.class, event ->
      delivered(state.withStatus(DELIVERED)));

    return builder.build();
  }

  private Behavior delivered(OrderState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(PlaceOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(AcceptOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(StartOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(FinishOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(DeliverOrder.class, this::alreadyDone);

    return builder.build();
  }

  private Behavior cancelled(OrderState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(PlaceOrder.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(CancelOrder.class, this::alreadyDone);

    return builder.build();
  }

  private void alreadyDone(Object command, ReadOnlyCommandContext<Done> ctx) {
    ctx.reply(Done.getInstance());
  }
}
