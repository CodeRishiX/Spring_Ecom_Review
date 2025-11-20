package codex_rishi.ecom_spring.repository;

import codex_rishi.ecom_spring.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
