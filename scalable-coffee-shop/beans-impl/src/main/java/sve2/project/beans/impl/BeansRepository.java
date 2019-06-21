package sve2.project.beans.impl;

import org.pcollections.PSequence;
import sve2.project.beans.api.Bean;

import java.util.concurrent.CompletionStage;

public interface BeansRepository {
  CompletionStage<PSequence<Bean>> getBeans();
}
