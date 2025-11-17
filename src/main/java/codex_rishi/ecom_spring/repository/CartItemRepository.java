package codex_rishi.ecom_spring.repository;

import codex_rishi.ecom_spring.model.CartItem;
import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByProduct_IdAndUser_Id(Long productId, Long userId);  //select * from CartItem where product_id = ? and user_id = ?
    void deleteByProduct_IdAndUser_Id(Long productId, Long userId);// delete from CartItem where product_id = ? and user_id = ?
    void deleteByProduct_Id(Long productId);
    List<CartItem> findAllByUser_Id(Long userId);// select * from CartItem where user_id = ?

    // Custom query to find a cart item by user and product
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}
