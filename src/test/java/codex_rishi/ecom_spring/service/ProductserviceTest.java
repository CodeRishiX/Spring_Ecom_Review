package codex_rishi.ecom_spring.service;
import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductserviceTest {
    @Mock
    private ProductRepo productRepository;
    @InjectMocks
    private Productservice productService;
    private Product product;

    // Convert LocalDate → Date
    LocalDate localDate = LocalDate.of(2024, 6, 21);
    Date releaseDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1);
        product.setName("Laptop");
        product.setBrand("Dell");
        product.setPrice(BigDecimal.valueOf(50000.0));
        product.setDescription("A powerful Dell laptop");
        product.setCategory("Electronics");
        product.setReleaseDate(new Date());
        product.setQuantity(5);
        product.setImage("laptop.jpg");
        product.setImagetype("image/jpeg");
        product.setImagedata(null);
    }

    @Test
    void testGetProductById() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        // Act
        Product result = productService.getProductByID(1);
        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals("Dell", result.getBrand());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void getAllProductsTest()
    {
        when(productRepository.findAll()).thenReturn(List.of(product));
        List<Product> res=productService.getallproducts();
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Laptop", res.get(0).getName());
    }

    @Test
    void deleteProductTest() {
        productService.deleteproduct(1);
        verify(productRepository, times(1)).deleteById(1);
    }

     @Test
     void checknewarrival()
     {
//         when(productRepository.newarrival(9,2025)).thenReturn(Optional.of(product));
          productService.newarrival();
          verify(productRepository,times(1)).newarrival(any(Date.class));
     }

     @Test
     void SearchProductTest()
     {
         when(productRepository.searchproducts("Laptop")).thenReturn(List.of(product));
         List<Product>res=productService.searchproduct("Laptop");
         assertNotNull(res);
         assertEquals("Laptop",res.getFirst().getName());
     }

    @Test
    void updateProductTest() throws IOException {
        Product existingProduct = new Product();
        existingProduct.setId(2);
        existingProduct.setName("New Laptop");
        existingProduct.setBrand("Dell");
        existingProduct.setPrice(BigDecimal.valueOf(5000.256));
        existingProduct.setDescription("bhaloi besh");
        existingProduct.setQuantity(25);
        existingProduct.setCategory("Laptop");
        existingProduct.setReleaseDate(releaseDate);


        Product updatedProduct = new Product();
        updatedProduct.setId(2);
        updatedProduct.setName("Superbb");
        updatedProduct.setBrand("hp");
        updatedProduct.setPrice(BigDecimal.valueOf(50550.256));
        updatedProduct.setDescription("bhaloi ");
        updatedProduct.setQuantity(24);
        updatedProduct.setCategory("Computer");
        updatedProduct.setReleaseDate(releaseDate);

        MultipartFile mockImage = new MockMultipartFile("file", "new.jpg", "image/jpeg", "fake".getBytes());

        when(productRepository.findById(2)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateproduct(2, updatedProduct, mockImage);

        assertNotNull(result);
        assertEquals("Superbb", result.getName());
        assertEquals("hp", result.getBrand());
        assertEquals("new.jpg", result.getImage());
    }

    @Test
    void AddProductTest() throws IOException {


        Product newproduct = new Product();
        newproduct.setId(2);
        newproduct.setName("New Laptop");
        newproduct.setBrand("Dell");
        newproduct.setPrice(BigDecimal.valueOf(5000.256));
        newproduct.setDescription("bhaloi besh");
        newproduct.setQuantity(25);
        newproduct.setCategory("Laptop");
        newproduct.setReleaseDate(releaseDate);

        MultipartFile mockImage = new MockMultipartFile("file", "new.jpg", "image/jpeg", "fake".getBytes());

        // 🔹 Tell Mockito what to return when save() is called
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // 🔹 Act
        Product result = productService.addproduct(newproduct, mockImage);

        // 🔹 Assert (check the output)
        assertNotNull(result);
        assertEquals("Dell", result.getBrand());
        assertEquals("new.jpg", result.getImage());

        // 🔹 Verify (check interaction)
        verify(productRepository, times(1)).save(any(Product.class));
    }



}
