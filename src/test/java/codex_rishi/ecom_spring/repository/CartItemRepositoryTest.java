package codex_rishi.ecom_spring.repository;

import codex_rishi.ecom_spring.model.CartItem;
import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.model.Role;
import codex_rishi.ecom_spring.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemRepositoryTest {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void findByProduct_IdAndUser_Id() throws Exception {

        // Create product with valid constructor values
        Product p1 = new Product(
                1,
                "Laptop",
                new BigDecimal("999.99"),
                "Great laptop",
                "Electronics",
                "Dell",
                new SimpleDateFormat("yyyy").parse("2025"),
                10,
                "image.jpg",
                "image/jpg",
                new byte[]{1}
        );

        User u1 = new User(
                null,
                "hdh@gmail.com",
                "vh",
                "profile.jpg",
                Role.USER
        );

        productRepo.save(p1);
        userRepository.save(u1);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(p1);
        cartItem.setUser(u1);
        cartItem.setQuantity(3);
        cartItem.setAddedAt(LocalDateTime.now());

        cartItemRepository.save(cartItem);

        CartItem result = cartItemRepository.findByProduct_IdAndUser_Id((long) p1.getId(), u1.getId());

        assertNotNull(result);
        assertEquals(3, result.getQuantity());
    }

    @Test
    void findAllByUser_Id_shouldReturnUserCartItems() throws Exception {

        Product p1 = new Product(
                6,
                "Laptop",
                new BigDecimal("999.99"),
                "Great laptop",
                "Electronics",
                "Dell",
                new SimpleDateFormat("yyyy").parse("2025"),
                10,
                "image.jpg",
                "image/jpg",
                new byte[]{1}
        );

        Product p2 = new Product(
                5,
                "Keyboard",
                new BigDecimal("49.99"),
                "Mechanical",
                "Electronics",
                "Logitech",
                new SimpleDateFormat("yyyy").parse("2023"),
                20,
                "image2.jpg",
                "image/jpg",
                new byte[]{1}
        );

        User u1 = new User(
                null,
                "user@gmail.com",
                "User1",
                "img.jpg",
                Role.USER
        );

        Product savedP1 = productRepo.save(p1);
        Product savedP2 = productRepo.save(p2);
        User savedU1 = userRepository.save(u1);

        CartItem c1 = new CartItem();
        c1.setProduct(savedP1);
        c1.setUser(savedU1);
        c1.setQuantity(2);
        c1.setAddedAt(LocalDateTime.now());

        CartItem c2 = new CartItem();
        c2.setProduct(savedP2);
        c2.setUser(savedU1);
        c2.setQuantity(5);
        c2.setAddedAt(LocalDateTime.now());

        cartItemRepository.save(c1);
        cartItemRepository.save(c2);

        List<CartItem> result = cartItemRepository.findAllByUser_Id(savedU1.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(ci -> ci.getProduct().getName().equals("Laptop")));
        assertTrue(result.stream().anyMatch(ci -> ci.getProduct().getName().equals("Keyboard")));
    }
    @Test
    void deleteByProduct_IdAndUser_Id_shouldDeleteCartItem() throws Exception {

        Product p1 = new Product(
                5,
                "Laptop",
                new BigDecimal("999.99"),
                "Great laptop",
                "Electronics",
                "Dell",
                new SimpleDateFormat("yyyy").parse("2025"),
                10,
                "image.jpg",
                "image/jpg",
                new byte[]{1}
        );

        User u1 = new User(
                null,
                "user@gmail.com",
                "User1",
                "img.jpg",
                Role.USER
        );

        Product savedP1 = productRepo.save(p1);
        User savedU1 = userRepository.save(u1);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(savedP1);
        cartItem.setUser(savedU1);
        cartItem.setQuantity(4);
        cartItem.setAddedAt(LocalDateTime.now());

        cartItemRepository.save(cartItem);

        cartItemRepository.deleteByProduct_IdAndUser_Id((long) savedP1.getId(), savedU1.getId());

        List<CartItem> afterDelete = cartItemRepository.findAllByUser_Id(savedU1.getId());

        assertTrue(afterDelete.isEmpty());
    }

}
