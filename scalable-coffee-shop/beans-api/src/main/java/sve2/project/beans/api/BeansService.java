package sve2.project.beans.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import org.pcollections.PSequence;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface BeansService extends Service {
  String BEANS_TOPIC = "beans";

  @Override
  default Descriptor descriptor() {
    return named("beans")
      .withCalls(
        namedCall("/beans", this::getBeans),
        namedCall("/beans", this::storeBeans)
      )
      .withTopics(topic(BEANS_TOPIC, this::beansTopic));
  }

  ServiceCall<NotUsed, PSequence<Bean>> getBeans();

  ServiceCall<StoreBeansRequest, Done> storeBeans();

  Topic<BeansEvent> beansTopic();
}
