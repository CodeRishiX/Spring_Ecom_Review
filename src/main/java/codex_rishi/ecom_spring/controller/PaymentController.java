package codex_rishi.ecom_spring.controller;
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

    //  Create Razorpay Order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(data));
    }

    //  Verify Razorpay Payment
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(paymentService.verifyPaymentSignature(data));
    }



}
