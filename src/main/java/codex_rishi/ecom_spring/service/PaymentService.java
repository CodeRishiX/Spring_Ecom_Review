// Clean and Corrected PaymentService.java
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

    private RazorpayClient razorpayClient;

    @Autowired
    private RazorpayConfig razorpayConfig;

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

    @PostConstruct
    public void init() throws Exception {
        this.razorpayClient = new RazorpayClient(
                razorpayConfig.getKey(),
                razorpayConfig.getSecret()
        );
    }

    // =====================================================================
    // CREATE RAZORPAY ORDER
    // =====================================================================
    public Map<String, Object> createRazorpayOrder(Map<String, Object> data) {
        try {
            Long userId = Long.valueOf(data.get("userId").toString());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CartItem> cartItems = cartItemRepository.findAllByUser_Id(userId);
            if (cartItems.isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            BigDecimal subtotal = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                BigDecimal price = item.getProduct().getPrice();
                BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
                subtotal = subtotal.add(price.multiply(qty));
            }

            BigDecimal deliveryFee = BigDecimal.valueOf(59);
            BigDecimal platformFee = BigDecimal.valueOf(12);
            BigDecimal discount = subtotal.compareTo(BigDecimal.valueOf(5000)) > 0
                    ? BigDecimal.valueOf(500)
                    : BigDecimal.ZERO;

            BigDecimal grandTotal = subtotal
                    .add(deliveryFee)
                    .add(platformFee)
                    .subtract(discount);

            int razorpayAmount = grandTotal.multiply(BigDecimal.valueOf(100)).intValue();

            codex_rishi.ecom_spring.model.Order order =
                    codex_rishi.ecom_spring.model.Order.builder()
                            .user(user)
                            .totalAmount(grandTotal)
                            .status(OrderStatus.PENDING)
                            .createdAt(LocalDateTime.now())
                            .build();

            order = orderRepository.save(order);

            JSONObject options = new JSONObject();
            options.put("amount", razorpayAmount);
            options.put("currency", "INR");
            options.put("receipt", "order_rcpt_" + order.getId());

            Order razorpayOrder = razorpayClient.orders.create(options);

            order.setRazorpayOrderId(razorpayOrder.get("id"));
            orderRepository.save(order);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", razorpayOrder.get("id"));
            response.put("amount", razorpayAmount);
            response.put("currency", "INR");
            response.put("internalOrderId", order.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("key", razorpayConfig.getKey());

            return response;

        } catch (Exception ex) {
            throw new RuntimeException("Create order failed: " + ex.getMessage());
        }
    }

    // =====================================================================
    // VERIFY PAYMENT SIGNATURE
    // =====================================================================
    public Map<String, Object> verifyPaymentSignature(Map<String, Object> data) {

        Map<String, Object> resp = new HashMap<>();

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

            // ------------------------------
            // ❌ FAILURE CASE
            // ------------------------------
            if (!isValid) {
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);

//                emailService.sendOrderFailureEmail(
//                        order.getUser().getEmail(),
//                        order.getUser().getName(),
//                        order.getId().toString(),
//                        "Invalid payment signature"
//                );

                resp.put("status", "failed");
                resp.put("message", "Invalid signature");
                return resp;
            }

            // ------------------------------
            // ✅ SUCCESS CASE
            // ------------------------------
            order.setStatus(OrderStatus.PAID);
            order.setPaymentId(razorpayPaymentId);
            orderRepository.save(order);

//            emailService.sendOrderSuccessEmail(
//                    order.getUser().getEmail(),
//                    order.getUser().getName(),
//                    order.getId().toString(),
//                    order.getTotalAmount()
//            );

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

            resp.put("status", "success");
            resp.put("orderId", order.getId());
            resp.put("paymentId", order.getPaymentId());
            return resp;

        } catch (Exception ex) {
            resp.put("status", "failed");
            resp.put("message", ex.getMessage());
            return resp;
        }
    }
}
