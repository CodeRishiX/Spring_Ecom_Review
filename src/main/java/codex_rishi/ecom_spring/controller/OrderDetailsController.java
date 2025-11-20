package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.model.CartItem;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.UserRepository;
import codex_rishi.ecom_spring.service.OrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/orderdetails")
public class OrderDetailsController {
//    @Autowired
//    private CartService cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderDetailsService orderDetailsService;

    @GetMapping
    public List<CartItem> getUserCart(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email);
        return orderDetailsService.getCartItemsByUser(user);
    }

}
