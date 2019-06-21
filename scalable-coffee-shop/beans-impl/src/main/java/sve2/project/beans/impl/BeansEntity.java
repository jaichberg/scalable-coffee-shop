package sve2.project.beans.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import sve2.project.beans.api.BeansEvent;
import sve2.project.beans.api.events.BeansFetched;
import sve2.project.beans.api.events.BeansNotAvailable;
import sve2.project.beans.api.events.BeansReserved;
import sve2.project.beans.api.events.BeansStored;
import sve2.project.beans.impl.commands.ReserveBeans;
import sve2.project.beans.impl.commands.StoreBeans;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class BeansEntity extends PersistentEntity<BeansCommand, BeansEvent, Optional<BeansState>> {

  @Override
  public Behavior initialBehavior(Optional<Optional<BeansState>> snapshotState) {
    Optional<BeansState> state = snapshotState.flatMap(Function.identity());

    if (!state.isPresent()) {
      return notCreated();
    } else {
      return created(state.get());
    }
  }

  private Behavior notCreated() {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.empty());

    // Command Handlers:
    builder.setCommandHandler(StoreBeans.class,
      (cmd, ctx) -> ctx.thenPersist(
        new BeansStored(cmd.getBeanOrigin(), cmd.getAmount())));

    builder.setCommandHandler(ReserveBeans.class,
      (cmd, ctx) -> ctx.thenPersist(
        new BeansNotAvailable(cmd.getOrderId())));

    // Event Handlers:
    builder.setEventHandlerChangingBehavior(BeansStored.class, event ->
      created(new BeansState(event.getBeanOrigin(), event.getAmount())));

    // BeansNotAvailable doesn't change the beans state but
    // should cause a change in the order state which is
    // applied by the orders service.
    builder.setEventHandler(BeansNotAvailable.class, event ->
      Optional.empty());

    return builder.build();
  }

  private Behavior created(BeansState state) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(state));

    // Command Handlers:
    builder.setCommandHandler(StoreBeans.class,
      (cmd, ctx) -> ctx.thenPersist(
        new BeansStored(cmd.getBeanOrigin(), cmd.getAmount())));

    builder.setCommandHandler(ReserveBeans.class,
      (cmd, ctx) -> {
        if (state.getAmount() > 0) {
          return ctx.thenPersistAll(Arrays.asList(
            new BeansFetched(cmd.getBeanOrigin()),
            new BeansReserved(cmd.getOrderId())));
        } else {
          return ctx.thenPersist(new BeansNotAvailable(cmd.getOrderId()));
        }
      });

    // Event Handlers:
    builder.setEventHandler(BeansFetched.class, event ->
      Optional.of(state.addAmount(-1)));

    // BeansNotAvailable and BeansReserved don't change
    // the beans state but should cause changes in the
    // order state which are applied by the orders service.
    builder.setEventHandler(BeansNotAvailable.class, event -> Optional.of(state));
    builder.setEventHandler(BeansReserved.class, event -> Optional.of(state));

    builder.setEventHandler(BeansStored.class, event ->
      Optional.of(state.addAmount(event.getAmount())));

    return builder.build();
  }
}
