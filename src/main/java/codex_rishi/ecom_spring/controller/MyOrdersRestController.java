package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.model.*;
import codex_rishi.ecom_spring.repository.CartItemRepository;
import codex_rishi.ecom_spring.repository.OrderRepository;
import codex_rishi.ecom_spring.repository.UserRepository;
import codex_rishi.ecom_spring.service.MyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MyOrdersRestController {

    @Autowired
    private MyOrderService myOrderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        try {
            // auth check
            if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Login required"));
            }

            User user = userRepository.findByEmail(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            List<Order> orders = myOrderService.getOrdersForUser(user.getId());
            List<Map<String, Object>> resp = new ArrayList<>();

            for (Order order : orders) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("totalAmount", order.getTotalAmount());
                orderMap.put("status", order.getStatus() != null ? order.getStatus().toString() : "UNKNOWN");
                orderMap.put("createdAt", order.getCreatedAt());

                List<Map<String, Object>> items = new ArrayList<>();

                // If PAID => use saved order items (they exist only after payment success)
                if (order.getStatus() == OrderStatus.PAID) {
                    List<OrderItem> orderItems = order.getOrderItems();
                    if (orderItems != null) {
                        for (OrderItem oi : orderItems) {
                            if (oi == null || oi.getProduct() == null) continue;
                            Map<String, Object> itemMap = new HashMap<>();
                            Map<String, Object> productMap = new HashMap<>();
                            productMap.put("id", oi.getProduct().getId());
                            productMap.put("name", oi.getProduct().getName() != null ? oi.getProduct().getName() : "Product");
                            productMap.put("imageUrl", "/api/product/" + oi.getProduct().getId() + "/image");
                            itemMap.put("product", productMap);
                            itemMap.put("quantity", oi.getQuantity());
                            items.add(itemMap);
                        }
                    }
                } else {
                    // PENDING or FAILED => show current cart items for that user (gives product name + image)
                    List<CartItem> cartItems = cartItemRepository.findAllByUser_Id(user.getId());
                    if (cartItems != null) {
                        for (CartItem ci : cartItems) {
                            if (ci == null || ci.getProduct() == null) continue;
                            Map<String, Object> itemMap = new HashMap<>();
                            Map<String, Object> productMap = new HashMap<>();
                            productMap.put("id", ci.getProduct().getId());
                            productMap.put("name", ci.getProduct().getName() != null ? ci.getProduct().getName() : "Product");
                            productMap.put("imageUrl", "/api/product/" + ci.getProduct().getId() + "/image");
                            itemMap.put("product", productMap);
                            itemMap.put("quantity", ci.getQuantity());
                            items.add(itemMap);
                        }
                    }
                }

                orderMap.put("items", items);

                // convenience fields frontend expects at root
                if (!items.isEmpty()) {
                    Map<String, Object> firstProduct = (Map<String, Object>) items.get(0).get("product");

                    orderMap.put("productName",
                            String.valueOf(firstProduct.getOrDefault("name", "Product")));

                    orderMap.put("imageUrl",
                            String.valueOf(firstProduct.getOrDefault("imageUrl",
                                    "https://placehold.co/100x100?text=No+Image")));
                } else {
                    orderMap.put("productName", "Product");
                    orderMap.put("imageUrl", "https://placehold.co/100x100?text=No+Image");
                }


                resp.add(orderMap);
            }

            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch orders", "detail", ex.getMessage()));
        }
    }

}
