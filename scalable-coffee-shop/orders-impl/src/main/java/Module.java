import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sve2.project.barista.api.BaristaService;
import sve2.project.beans.api.BeansService;
import sve2.project.orders.api.OrdersService;
import sve2.project.orders.impl.OrdersRepository;
import sve2.project.orders.impl.OrdersRepositoryImpl;
import sve2.project.orders.impl.OrdersServiceImpl;

public class Module extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindService(OrdersService.class, OrdersServiceImpl.class);

    bindClient(BeansService.class);
    bindClient(BaristaService.class);

    bind(OrdersRepository.class).to(OrdersRepositoryImpl.class);
  }
}
