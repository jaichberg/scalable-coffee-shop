package sve2.project.barista.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import sve2.project.barista.api.BaristaEvent;
import sve2.project.barista.api.events.CoffeeBrewFinished;
import sve2.project.barista.api.events.CoffeeBrewStarted;
import sve2.project.barista.api.events.CoffeeDelivered;
import sve2.project.barista.impl.commands.DeliverCoffee;
import sve2.project.barista.impl.commands.FinishBrewingCoffee;
import sve2.project.barista.impl.commands.StartBrewingCoffee;

import java.util.Optional;
import java.util.function.Function;

import static sve2.project.barista.impl.BaristaStatus.*;

public class BaristaEntity extends PersistentEntity<BaristaCommand, BaristaEvent, Optional<BaristaState>> {
  @Override
  public Behavior initialBehavior(Optional<Optional<BaristaState>> snapshotState) {
    Optional<BaristaState> state = snapshotState.flatMap(Function.identity());

    if (!state.isPresent()) {
      return notCreated();
    } else {
      switch (state.get().getStatus()) {
        case COFFEE_STARTED:
          return started(state.get());
        case COFFEE_FINISHED:
          return finished(state.get());
        case COFFEE_DELIVERED:
          return delivered(state.get());
        default:
          throw new IllegalStateException();
      }
    }
  }

  private Behavior notCreated() {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.empty());

    // Command Handlers:
    builder.setCommandHandler(StartBrewingCoffee.class,
      (cmd, ctx) -> ctx.thenPersist(
        new CoffeeBrewStarted(cmd.getOrderId())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(CoffeeBrewStarted.class, event ->
      started(new BaristaState(event.getOrderId(), COFFEE_STARTED)));

    return builder.build();
  }

  private Behavior started(BaristaState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(StartBrewingCoffee.class, this::alreadyDone);

    builder.setCommandHandler(FinishBrewingCoffee.class,
      (cmd, ctx) -> ctx.thenPersist(
        new CoffeeBrewFinished(cmd.getOrderId())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(CoffeeBrewFinished.class, event ->
      finished(new BaristaState(event.getOrderId(), COFFEE_FINISHED)));

    return builder.build();
  }

  private Behavior finished(BaristaState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(StartBrewingCoffee.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(FinishBrewingCoffee.class, this::alreadyDone);

    builder.setCommandHandler(DeliverCoffee.class,
      (cmd, ctx) -> ctx.thenPersist(
        new CoffeeDelivered(cmd.getOrderId())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(CoffeeDelivered.class, event ->
      finished(new BaristaState(event.getOrderId(), COFFEE_DELIVERED)));

    return builder.build();
  }

  private Behavior delivered(BaristaState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setReadOnlyCommandHandler(StartBrewingCoffee.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(FinishBrewingCoffee.class, this::alreadyDone);
    builder.setReadOnlyCommandHandler(DeliverCoffee.class, this::alreadyDone);

    return builder.build();
  }

  private void alreadyDone(Object command, ReadOnlyCommandContext<Done> ctx) {
    ctx.reply(Done.getInstance());
  }
}
