package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.model.CartItem;
import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.model.Role;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    CartItemRepository cartItemRepository;

    @InjectMocks
    CartService cartService;

    Product product;
    User user;
    CartItem cartItem;

    CartServiceTest() throws ParseException {}

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.openMocks(this);

        product = new Product(
                54,
                "sh",
                new BigDecimal("554.25"),
                "dn",
                "dd",
                "ss",
                new SimpleDateFormat("yyyy").parse("2025"),
                33,
                "ss",
                "dd",
                new byte[]{1, 2, 3}
        );

        user = new User(1L, "gmail", "Rishi", "jpg", Role.ADMIN);

        cartItem = new CartItem();
        cartItem.setId(15L);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(10);
        cartItem.setAddedAt(LocalDateTime.now());
    }

    // ---------------------- ADD TO CART TESTS ----------------------

    @Test
    void addToCart_createsNewItem() {
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        cartService.addToCart(user, product, 5);

        verify(cartItemRepository, times(1)).save(argThat(item ->
                item.getUser().equals(user) &&
                        item.getProduct().equals(product) &&
                        item.getQuantity() == 5
        ));
    }

    @Test
    void addToCart_updatesExistingItem() {
        CartItem existing = new CartItem();
        existing.setUser(user);
        existing.setProduct(product);
        existing.setQuantity(10);

        when(cartItemRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(existing));

        cartService.addToCart(user, product, 5);

        verify(cartItemRepository, times(1)).save(argThat(item ->
                item.getQuantity() == 15 &&
                        item.getUser().equals(user) &&
                        item.getProduct().equals(product)
        ));
    }

    @Test
    void addToCart_quantityZero_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(user, product, 0));
    }

    @Test
    void addToCart_negativeQuantity_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(user, product, -5));
    }

    @Test
    void addToCart_quantityExceedsStock_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(user, product, 50)); // stock = 33
    }

    @Test
    void addToCart_existingItemExceedsStock_throwsException() {
        CartItem existing = new CartItem();
        existing.setUser(user);
        existing.setProduct(product);
        existing.setQuantity(30);

        when(cartItemRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(user, product, 10)); // 30 + 10 = 40 > 33 stock
    }

    @Test
    void addToCart_nullUser_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(null, product, 5));
    }

    @Test
    void addToCart_nullProduct_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(user, null, 5));
    }

    // ---------------------- GET CART ITEMS TEST ----------------------

    @Test
    void getCartItemsByUser_returnsCorrectItems() {
        when(cartItemRepository.findAllByUser_Id(1L)).thenReturn(List.of(cartItem));

        List<CartItem> result = cartService.getCartItemsByUser(user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(15L, result.get(0).getId());
        assertEquals(1L, result.get(0).getUser().getId());

        verify(cartItemRepository, times(1)).findAllByUser_Id(1L);
    }

    // ---------------------- REMOVE TEST ----------------------

    @Test
    void removeFromCart_deletesCorrectItem() {
        cartService.removeFromCart(1L, 54L);

        verify(cartItemRepository, times(1))
                .deleteByProduct_IdAndUser_Id(54L, 1L);
    }
}
