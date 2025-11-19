package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.repository.CartItemRepository;
import codex_rishi.ecom_spring.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class Productservice {
    @Autowired
    private ProductRepo repo;
    @Autowired
    private CartItemRepository cartItemRepository;

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable);
    }


    public Product getProductByID(int id) {
        return repo.findById(id).get();
    }


    public Product addproduct(Product product, MultipartFile imagefile) throws IOException {  // Add product logic here
        product.setImage(imagefile.getOriginalFilename());
        product.setImagetype(imagefile.getContentType());
        product.setImagedata(imagefile.getBytes());

        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Brand name cannot be empty.");
        }

        if (!product.getBrand().matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("❌ Brand name can contain only alphabets.");
        }

        if (product.getCategory()== null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Category cannot be empty");
        }
        if (!product.getCategory().matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("❌ Category can contain only alphabets.");
        }


        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Product name cannot be empty.");
        }
        if ( product.getName().matches("\\d+")) {
            throw new IllegalArgumentException("❌ Product name cannot be  whole  numeric.");
        }

        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Description cannot be empty.");
        }
        if ( product.getDescription().matches("\\d+")) {
            throw new IllegalArgumentException("❌ Description  cannot be  whole numeric.");
        }

        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("❌ Price cannot be negative.");
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("❌ Price cannot be zero.");
        }

        int  quantity = product.getQuantity();

        if (quantity <= 0) {
            throw new IllegalArgumentException("❌ Quantity must be at least 1.");
        }
        if (quantity > 100000) {
            throw new IllegalArgumentException(
                    "❌ Invalid quantity. You cannot produce " + quantity + " units.");
        }
        Date cutoff = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();
        Date now = new Date();

        Calendar maxFuture = Calendar.getInstance();
        maxFuture.add(Calendar.YEAR, 2);
        Date maxAllowed = maxFuture.getTime();

        Date release = product.getReleaseDate();

        if (release == null || release.before(cutoff)) {
            throw new IllegalArgumentException("❌ Release date must be on or after January 2024.");
        }

        if (release.after(maxAllowed)) {
            throw new IllegalArgumentException("❌ Release date cannot be more than 2 years in the future.");
        }


        String type = imagefile.getContentType();
        if (!type.equals("image/jpeg") &&
                !type.equals("image/png") &&
                !type.equals("image/jpg")) {
            throw new IllegalArgumentException("❌ Only JPG, JPEG, or PNG images are allowed.");
        }
        if (imagefile.getSize() > 1_048_576) {
            throw new IllegalArgumentException("❌ Image size must be less than 1MB.");
        }

        return repo.save(product);
    }

    public Product updateproduct(int id, Product product, MultipartFile imagefile) throws IOException {

        Product existingProduct = repo.findById(id).orElse(null);
        if (existingProduct == null) {
            return null;
        }

        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Brand name cannot be empty.");
        }
        if (!product.getBrand().matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("❌ Brand name can contain only alphabets.");
        }

        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Category cannot be empty.");
        }
        if (!product.getCategory().matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("❌ Category can contain only alphabets.");
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Product name cannot be empty.");
        }
        if (product.getName().matches("\\d+")) {
            throw new IllegalArgumentException("❌ Product name cannot be whole numeric.");
        }

        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Description cannot be empty.");
        }
        if (product.getDescription().matches("\\d+")) {
            throw new IllegalArgumentException("❌ Description cannot be whole numeric.");
        }

        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("❌ Price cannot be negative.");
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("❌ Price cannot be zero.");
        }

        int quantity = product.getQuantity();
        if (quantity <= 0) {
            throw new IllegalArgumentException("❌ Quantity must be at least 1.");
        }
        if (quantity > 100000) {
            throw new IllegalArgumentException("❌ Invalid quantity. You cannot produce " + quantity + " units.");
        }

        Date cutoff = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();
        if (product.getReleaseDate() == null || product.getReleaseDate().before(cutoff)) {
            throw new IllegalArgumentException("❌ Release date must be in or after 2024.");
        }

        if (imagefile != null && !imagefile.isEmpty()) {
            if (imagefile.getSize() > 1_048_576) {
                throw new IllegalArgumentException("❌ Image size must be less than 1MB.");
            }
            String type = imagefile.getContentType();
            if (!type.equals("image/jpeg") &&
                    !type.equals("image/png") &&
                    !type.equals("image/jpg")) {
                throw new IllegalArgumentException("❌ Only JPG, JPEG, or PNG images are allowed.");
            }

            existingProduct.setImage(imagefile.getOriginalFilename());
            existingProduct.setImagetype(type);
            existingProduct.setImagedata(imagefile.getBytes());
        }

        existingProduct.setBrand(product.getBrand());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setReleaseDate(product.getReleaseDate());

        return repo.save(existingProduct);
    }
    public void deleteproduct(int id) {
        cartItemRepository.deleteByProduct_Id((long) id);
        repo.deleteById(id);
    }

    public List<Product> searchproduct(String keyword) {
            return repo.searchproducts(keyword);
    }

    public List<Product> newarrival() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        Date fromDate = Date.from(sevenDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return repo.newarrival(fromDate);
    }
}


