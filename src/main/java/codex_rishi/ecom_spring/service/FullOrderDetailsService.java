package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.dto.OrderDetailsDTO;
import codex_rishi.ecom_spring.dto.OrderItemDTO;
import codex_rishi.ecom_spring.model.Order;
import codex_rishi.ecom_spring.model.OrderItem;
import codex_rishi.ecom_spring.repository.OrderItemRepository;
import codex_rishi.ecom_spring.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FullOrderDetailsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderDetailsDTO getOrderDetails(Long orderId, Long userId) {

        // 1️⃣ Fetch order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2️⃣ Validate ownership
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to this order");
        }

        // 3️⃣ Prepare DTO
        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setId(order.getId());
        dto.setPaymentId(order.getPaymentId());
        dto.setRazorpayOrderId(order.getRazorpayOrderId());
        dto.setStatus(order.getStatus().toString());
        dto.setCreatedAt(order.getCreatedAt().toString());
        dto.setTotalAmount(order.getTotalAmount().doubleValue());

        // 4️⃣ Fetch all items of this order
        List<OrderItem> items = orderItemRepository.findByOrder(order);

        List<OrderItemDTO> itemDTOs = items.stream().map(item -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setName(item.getProduct().getName());
            i.setPrice(item.getPrice().doubleValue());
            i.setQuantity(item.getQuantity());
            i.setImageUrl("/api/product/" + item.getProduct().getId() + "/image");
            return i;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);

        return dto;
    }
}
