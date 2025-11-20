package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.model.CartItem;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.OrderDetailsRepsository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderDetailsService {
    @Autowired
    private OrderDetailsRepsository orderDetailsRepsository;
    public List<CartItem> getCartItemsByUser(User user) {
        return orderDetailsRepsository.findAllByUser_Id(user.getId());
    }
}
