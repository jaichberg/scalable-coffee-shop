package sve2.project.barista.api;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.broker.Topic;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface BaristaService extends Service {
  String BARISTA_TOPIC = "barista";

  @Override
  default Descriptor descriptor() {
    return named("barista")
      .withTopics(topic(BARISTA_TOPIC, this::baristaTopic));
  }

  Topic<BaristaEvent> baristaTopic();
}
