package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.model.Product;
import codex_rishi.ecom_spring.service.Productservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")

public class Productcontroller {
    private static final Logger logger = LoggerFactory.getLogger(Productcontroller.class);
    @Autowired
    private Productservice service;

    @GetMapping("/products")
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return service.getAllProducts(page, size);
    }

    @GetMapping("/productdetails/{id}")
    public Product getProduct(@PathVariable int id)
    {
       return service.getProductByID(id);
    }

    @PostMapping("/addproduct")         // protect it admin
    public ResponseEntity<?> addproduct(@RequestPart Product product,
                                        @RequestPart MultipartFile imagefile) {
        try {
            Product product1 = service.addproduct(product, imagefile);
            return new ResponseEntity<>(product1, HttpStatus.CREATED);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/product/{productid}/image")         // PUBLIC      --> fetching image
    public ResponseEntity<byte[]>getimagebyproductid(@PathVariable int productid)

    {
        Product product = service.getProductByID(productid);
        byte[]imagefile=product.getImagedata();
        return ResponseEntity.ok().contentType(MediaType.valueOf(product.getImagetype()))
                .body(imagefile);
    }

    @PutMapping("/productupdate/{id}")   // protect it admin   --> update product
    public ResponseEntity<String> updateproduct(@PathVariable int id, @RequestPart Product product, @RequestPart(required = false) MultipartFile imagefile) throws IOException {
        logger.info("PUT /productupdate/{} called", id);
        Product product1 = service.updateproduct(id, product, imagefile);
        if (product1 != null) {
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    // Allow POST for update as well (for Postman or clients that can't send PUT with multipart)
    @PostMapping("/productupdate/{id}")
    public ResponseEntity<String> updateproductPost(@PathVariable int id, @RequestPart Product product, @RequestPart(required = false) MultipartFile imagefile) throws IOException {
        logger.info("POST /productupdate/{} called", id);
        Product product1 = service.updateproduct(id, product, imagefile);
        if (product1 != null) {
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteproduct(@PathVariable int id) {
        Product product = service.getProductByID(id);
        if (product != null) {
            service.deleteproduct(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }
    @RequestMapping("/products/search")  // PUBLIC
    public ResponseEntity<List<Product>>searchproducts(@RequestParam String keyword)
    {
        List<Product>products=service.searchproduct(keyword);
        logger.info("Searching with keyword: {}", keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @GetMapping("/New-Arrival")
    public Object newarrival(
            @RequestParam(value = "new", required = false) Boolean onlyNew,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        if (Boolean.TRUE.equals(onlyNew)) {
            // Return only new arrival list
            return service.newarrival();
        } else {
            // Return paginated products
            return service.getAllProducts(page, size);
        }
    }









}
