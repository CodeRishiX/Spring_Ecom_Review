package codex_rishi.ecom_spring.repository;
import codex_rishi.ecom_spring.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepoTest {

    @Autowired
    private ProductRepo productRepo;

    @Test
    void searchproducts_shouldReturnMatchingProducts() throws Exception {

        Product p1 = new Product(
                0,
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
                0,
                "Phone",
                new BigDecimal("599.99"),
                "Smartphone",
                "Electronics",
                "Samsung",
                new SimpleDateFormat("yyyy").parse("2025"),
                20,
                "image2.jpg",
                "image/jpg",
                new byte[]{1}
        );

        productRepo.save(p1);
        productRepo.save(p2);

        List<Product> result = productRepo.searchproducts("laptop");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void newarrival_check_workingornot() throws Exception {

        Product p1 = new Product(
                0,
                "Laptop",
                new BigDecimal("999.99"),
                "Gaming Laptop",
                "Electronics",
                "Dell",
                new SimpleDateFormat("yyyy").parse("2025"),
                10,
                "lap.jpg",
                "image/jpg",
                new byte[]{1}
        );

        Product p2 = new Product(
                0,
                "Phone",
                new BigDecimal("599.99"),
                "Smartphone",
                "Electronics",
                "Samsung",
                new SimpleDateFormat("yyyy").parse("2023"),
                20,
                "img.jpg",
                "image/jpg",
                new byte[]{1}
        );

        productRepo.save(p1);
        productRepo.save(p2);

        // fromDate = 2024 → Should return only p1 (2025)
        Date fromDate = new SimpleDateFormat("yyyy").parse("2024");

        List<Product> result = productRepo.newarrival(fromDate);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop"); // p1 only
    }

}
