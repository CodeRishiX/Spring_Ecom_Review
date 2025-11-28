package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.model.Order;
import codex_rishi.ecom_spring.model.OrderStatus;
import codex_rishi.ecom_spring.repository.OrderRepository;

import codex_rishi.ecom_spring.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
//    @Autowired
//    private EmailService emailService;

    /**
     * STEP 1: Create a Razorpay Order + Create INTERNAL ORDER (PENDING)
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(data));
    }

    /**
     * STEP 2: Verify Razorpay Payment → Update SAME Order
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(paymentService.verifyPaymentSignature(data));
    }

    @PostMapping("/failed/{orderId}")
    public ResponseEntity<?> markFailed(@PathVariable Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElse(null);

        if (order == null) {
            return ResponseEntity.status(404).body("Order not found");
        }

        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);

//        // ⭐ SEND FAILURE EMAIL ⭐
//        emailService.sendOrderFailureEmail(
//                order.getUser().getEmail(),
//                order.getUser().getName(),
//                order.getId().toString(),
//                "Payment was cancelled or failed"
//        );

        return ResponseEntity.ok("Order marked as FAILED");
    }
}
