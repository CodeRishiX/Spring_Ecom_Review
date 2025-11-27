package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.model.CartItem;
import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.CartItemRepository;
import codex_rishi.ecom_spring.repository.ProductRepo;

import codex_rishi.ecom_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private UserRepository userRepository;



    public void addToCart(User user, Product product, int quantity) {

        if (user == null || product == null) {
            throw new IllegalArgumentException("❌ Invalid user or product.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("❌ Quantity must be at least 1.");
        }

        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("❌ Not enough stock available.");
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQty = cartItem.getQuantity() + quantity;

            if (newQty > product.getQuantity()) {
                throw new IllegalArgumentException("❌ Quantity exceeds available stock.");
            }

            cartItem.setQuantity(newQty);
            cartItem.setAddedAt(java.time.LocalDateTime.now());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAddedAt(java.time.LocalDateTime.now());
            cartItemRepository.save(cartItem);
        }
    }

    public List<CartItem> getCartItemsByUser(User user) {
        return cartItemRepository.findAllByUser_Id(user.getId());
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.deleteByProduct_IdAndUser_Id(productId, userId);
    }


    public void updateProductQuantity(Long userId, Long productId, int quantity) {

        CartItem item = cartItemRepository
                .findByProduct_IdAndUser_Id(productId, userId);
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

}
