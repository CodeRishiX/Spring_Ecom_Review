package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/new-arrivals")
    public String newArrivalsPage() {
        return "New-Arrivals";
    }

    @GetMapping("/cart")
    public String cartPage() {
        return "Cart";
    }

    @GetMapping("/add-product")
    public String addProductPage() {
        return "add-product";
    }
    @GetMapping("/product/{id}")
    public String productPage(@PathVariable int id, Model model) {
        model.addAttribute("productId", id);
        return "product-details";
    }
    @GetMapping("/update-product")
    public String updateProductPage() {
        return "update-product";
    }


    @GetMapping("/order-details")
    public String orderDetails(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("userId", user.getId());
        return "order-details";
    }
    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam("orderId") Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "payment-success";
    }
    @GetMapping("/payment-failed")
    public String paymentFailedPage() {
        return "payment-failed";
    }
    @GetMapping("/orders")
    public String ordersPage() {
        return "orders";
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetailsPage(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "Full-order-details";  // order-details.html
    }

}
