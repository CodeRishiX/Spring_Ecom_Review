package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.model.Order;
import codex_rishi.ecom_spring.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyOrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
    }
}
