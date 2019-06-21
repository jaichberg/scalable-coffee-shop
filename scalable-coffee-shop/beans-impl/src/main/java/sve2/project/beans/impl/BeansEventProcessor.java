package sve2.project.beans.impl;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import sve2.project.beans.api.BeansEvent;
import sve2.project.beans.api.events.BeansFetched;
import sve2.project.beans.api.events.BeansStored;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatements;

public class BeansEventProcessor extends ReadSideProcessor<BeansEvent> {
  private final CassandraSession session;
  private final CassandraReadSide readSide;

  private PreparedStatement updateBeanAmountStatement;

  @Inject
  public BeansEventProcessor(CassandraSession session, CassandraReadSide readSide) {
    this.session = session;
    this.readSide = readSide;
  }

  @Override
  public PSequence<AggregateEventTag<BeansEvent>> aggregateTags() {
    return TreePVector.singleton(BeansEvent.TAG);
  }

  @Override
  public ReadSideHandler<BeansEvent> buildHandler() {
    return readSide.<BeansEvent>builder("beans_offset")
      .setGlobalPrepare(this::createTables)
      .setPrepare(tag -> this.prepareStatements())
      .setEventHandler(BeansStored.class, this::processBeansStored)
      .setEventHandler(BeansFetched.class, this::processBeansFetched)
      .build();
  }

  private CompletionStage<Done> createTables() {
    return session.executeCreateTable(
      "CREATE TABLE IF NOT EXISTS beans(" +
        "origin TEXT," +
        "amount COUNTER," +
        "PRIMARY KEY (origin)" +
      ")"
    );
  }

  private CompletionStage<Done> prepareStatements() {
    return CompletableFuture.allOf(
      prepareUpdateBeanAmountStatement().toCompletableFuture()
    ).thenApply(ignoredVoid -> Done.getInstance());
  }

  private CompletionStage<Done> prepareUpdateBeanAmountStatement() {
    return this.session
      .prepare("UPDATE beans SET amount = amount + ? WHERE origin = ?")
      .thenApply(statement -> {
        this.updateBeanAmountStatement = statement;
        return Done.getInstance();
      });
  }

  private CompletionStage<List<BoundStatement>> processBeansStored(BeansStored event) {
    return completedStatements(this.updateBeanAmountStatement.bind(
      event.getAmount(),
      event.getBeanOrigin()
    ));
  }

  private CompletionStage<List<BoundStatement>> processBeansFetched(BeansFetched event) {
    return completedStatements(this.updateBeanAmountStatement.bind(
      -1,
      event.getBeanOrigin()
    ));
  }
}
