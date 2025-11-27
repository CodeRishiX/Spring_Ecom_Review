package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.model.CartItem;
import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.CartItemRepository;
import codex_rishi.ecom_spring.repository.ProductRepo;
import codex_rishi.ecom_spring.repository.UserRepository;
import codex_rishi.ecom_spring.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/cart")
public class Cartcontroller {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    // ✅ POST /cart/add
    @PostMapping("/add")
    public CartItem addToCart(@RequestParam Long productId, // add productId as a request parameter to database
                              @RequestParam int quantity, // add quantity as a request parameter to database
                              @AuthenticationPrincipal OAuth2User oAuth2User) {// fetch user from DB
        //@AuthenticationPrincipal OAuth2User oAuth2User injects the currently authenticated user's OAuth2 details.

        String email = oAuth2User.getAttribute("email");// ✅ fetch email from OAuth2User from session
        User user = userRepository.findByEmail(email); // ✅ fetch from DB

        Product product = productRepository.findById(Math.toIntExact(productId))// ✅ fetch product from DB
                .orElseThrow(() -> new RuntimeException("Product not found"));// ✅ handle product not found
//Find product in database: Product product = productRepository.findById(Math.toIntExact(productId)) fetches the Product entity by its ID.
        cartService.addToCart(user, product, quantity); // Call updated service method
        return cartItemRepository.findByProduct_IdAndUser_Id(productId, user.getId()); // Return the added cart item
    }

    // @AuthenticationPrincipal injects the currently authenticated user's details (from the security context) into your controller method. It lets you access the logged-in user's information (like email, name, etc.) without needing to pass it from the client. This is useful for actions that depend on the current user, such as adding items to their cart.


    // ✅ GET /cart
    @GetMapping
    public List<CartItem> getUserCart(@AuthenticationPrincipal OAuth2User oAuth2User) {// fetch user from DB
        String email = oAuth2User.getAttribute("email");// ✅ fetch email from OAuth2User from session
        User user = userRepository.findByEmail(email); // ✅ fetch from DB // Find user in database
        return cartService.getCartItemsByUser(user);// ✅ get cart items from service
    }

    // ✅ DELETE /cart/remove/{productId}
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId,  // Changed from int to Long
                                                 @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.badRequest().body("User not authenticated");
        }

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("User email not found");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        try {
            cartService.removeFromCart(user.getId(), productId);  // Removed type casting
            return ResponseEntity.ok("✅ Removed from cart successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to remove item from cart: " + e.getMessage());
        }
    }

    // ✅ GET /cart/role

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Principal principal) {

        User user = userRepository.findByEmail(principal.getName());

        cartService.updateProductQuantity(user.getId(), productId, quantity);

        return ResponseEntity.ok("Cart updated");
    }


 
}
