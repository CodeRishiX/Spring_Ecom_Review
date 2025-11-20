package codex_rishi.ecom_spring.repository;

import codex_rishi.ecom_spring.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
