package codex_rishi.ecom_spring.repository;

import codex_rishi.ecom_spring.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepsository extends JpaRepository<CartItem, Long>  {

    List<CartItem> findAllByUser_Id(Long userId);
}
