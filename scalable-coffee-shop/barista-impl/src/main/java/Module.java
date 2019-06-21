import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sve2.project.barista.api.BaristaService;
import sve2.project.barista.impl.BaristaServiceImpl;
import sve2.project.orders.api.OrdersService;

public class Module extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindService(BaristaService.class, BaristaServiceImpl.class);

    bindClient(OrdersService.class);
  }
}

