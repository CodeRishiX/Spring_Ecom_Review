package codex_rishi.ecom_spring.repository;

import codex_rishi.ecom_spring.model.Order;
import codex_rishi.ecom_spring.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
