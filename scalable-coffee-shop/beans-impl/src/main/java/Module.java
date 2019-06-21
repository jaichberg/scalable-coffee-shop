import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sve2.project.beans.api.BeansService;
import sve2.project.beans.impl.BeansRepository;
import sve2.project.beans.impl.BeansRepositoryImpl;
import sve2.project.beans.impl.BeansServiceImpl;
import sve2.project.orders.api.OrdersService;

public class Module extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindService(BeansService.class, BeansServiceImpl.class);

    bindClient(OrdersService.class);

    bind(BeansRepository.class).to(BeansRepositoryImpl.class);
  }
}
