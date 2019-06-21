package sve2.project.beans.impl;

import com.datastax.driver.core.Row;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import sve2.project.beans.api.Bean;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class BeansRepositoryImpl implements BeansRepository {

  private static final String SELECT_BEANS = "SELECT * FROM beans";

  private final CassandraSession session;

  private static final Collector<Bean, ?, TreePVector<Bean>> pSequenceCollector =
    Collectors.collectingAndThen(Collectors.toList(), TreePVector::from);

  @Inject
  public BeansRepositoryImpl(CassandraSession session, ReadSide readSide) {
    this.session = session;

    readSide.register(BeansEventProcessor.class);
  }

  @Override
  public CompletionStage<PSequence<Bean>> getBeans() {
    return session
      .selectAll(SELECT_BEANS)
      .thenApply(this::mapBeans);
  }

  private TreePVector<Bean> mapBeans(List<Row> beans) {
    return beans.stream()
      .map(this::mapBean)
      .collect(pSequenceCollector);
  }

  private Bean mapBean(Row row) {
    return new Bean(
      row.getString("beanOrigin"),
      row.getInt("amount"));
  }
}
