package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.config.RazorpayConfig;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.Utils;

import codex_rishi.ecom_spring.model.*;
import codex_rishi.ecom_spring.repository.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    private RazorpayClient razorpayClient;  // ❗ Not autowired

    @Autowired
    private RazorpayConfig razorpayConfig;  // ✔ Inject config (key+secret)

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    // ----------------------------------------------------------
    // Initialize RazorpayClient AFTER config loads
    // ----------------------------------------------------------
    @PostConstruct
    public void init() throws Exception {
        this.razorpayClient = new RazorpayClient(
                razorpayConfig.getKey(),
                razorpayConfig.getSecret()
        );
    }

    // ================================================================
    // STEP 3 — CREATE RAZORPAY ORDER
    // ================================================================
    public Map<String, Object> createRazorpayOrder(Map<String, Object> data) {

        try {
            Long userId = Long.valueOf(data.get("userId").toString());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CartItem> cartItems = cartItemRepository.findAllByUser_Id(userId);
            if (cartItems.isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            BigDecimal total = BigDecimal.ZERO;

            for (CartItem item : cartItems) {
                BigDecimal price = item.getProduct().getPrice();
                BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
                total = total.add(price.multiply(qty));
            }

            int razorpayAmount = total.multiply(BigDecimal.valueOf(100)).intValue();

            // Create internal order
            codex_rishi.ecom_spring.model.Order order = codex_rishi.ecom_spring.model.Order.builder()
                    .user(user)
                    .totalAmount(total)
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            order = orderRepository.save(order);

            // Razorpay order options
            JSONObject options = new JSONObject();
            options.put("amount", razorpayAmount);
            options.put("currency", "INR");
            options.put("receipt", "order_rcpt_" + order.getId());

            // Create Razorpay order
            Order razorpayOrder = razorpayClient.orders.create(options);

            // Save razorpay order id in DB
            order.setRazorpayOrderId(razorpayOrder.get("id"));
            orderRepository.save(order);

            // Response to frontend
            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", razorpayOrder.get("id"));
            response.put("amount", razorpayAmount);
            response.put("currency", "INR");
            response.put("internalOrderId", order.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("key", razorpayConfig.getKey());  // ✔ Return key to frontend


            return response;

        } catch (Exception ex) {
            throw new RuntimeException("Create order failed: " + ex.getMessage());
        }

    }


    // =========================================================================
    // VERIFY SIGNATURE & FINALIZE ORDER
    // =========================================================================
    public Map<String, Object> verifyPaymentSignature(Map<String, Object> data) {

        try {
            String razorpayOrderId = data.get("razorpay_order_id").toString();
            String razorpayPaymentId = data.get("razorpay_payment_id").toString();
            String razorpaySignature = data.get("razorpay_signature").toString();

            Long internalOrderId = Long.valueOf(data.get("internalOrderId").toString());

            codex_rishi.ecom_spring.model.Order order =
                    orderRepository.findById(internalOrderId)
                            .orElseThrow(() -> new RuntimeException("Order not found"));

            JSONObject json = new JSONObject();
            json.put("razorpay_order_id", razorpayOrderId);
            json.put("razorpay_payment_id", razorpayPaymentId);
            json.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(json, razorpaySecret);

            if (!isValid) {
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                throw new RuntimeException("Invalid payment signature!");
            }

            // Mark as paid
            order.setStatus(OrderStatus.PAID);
            order.setPaymentId(razorpayPaymentId);
            orderRepository.save(order);

            Long userId = order.getUser().getId();
            List<CartItem> cartItems = cartItemRepository.findAllByUser_Id(userId);

            for (CartItem item : cartItems) {
                OrderItem oi = OrderItem.builder()
                        .order(order)
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .price(item.getProduct().getPrice())
                        .build();

                orderItemRepository.save(oi);
            }

            cartItemRepository.deleteAll(cartItems);

            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "success");
            resp.put("orderId", order.getId());
            resp.put("paymentId", order.getPaymentId());

            return resp;

        } catch (Exception ex) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "failed");
            resp.put("message", ex.getMessage());
            return resp;
        }
    }
}
