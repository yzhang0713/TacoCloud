package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.Order;

public interface JpaOrderRepository extends CrudRepository<Order, Long> {
}
