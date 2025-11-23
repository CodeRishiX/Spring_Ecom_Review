package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.dto.OrderDetailsDTO;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.UserRepository;
import codex_rishi.ecom_spring.service.FullOrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private FullOrderDetailsService fullOrderDetailsService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{orderId}")
    public OrderDetailsDTO getOrderDetails(
            @PathVariable Long orderId,
            Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        // principal.getName() = user email
        String email = principal.getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Long userId = user.getId();

        return fullOrderDetailsService.getOrderDetails(orderId, userId);

    }
}
