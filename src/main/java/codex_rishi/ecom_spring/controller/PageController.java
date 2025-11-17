package codex_rishi.ecom_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/new-arrivals")
    public String newArrivalsPage() {
        return "New-Arrivals";
    }

    @GetMapping("/cart")
    public String cartPage() {
        return "Cart";
    }

    @GetMapping("/add-product")
    public String addProductPage() {
        return "add-product";
    }
    @GetMapping("/product/{id}")
    public String productPage(@PathVariable int id, Model model) {
        model.addAttribute("productId", id);
        return "product-details";
    }
    @GetMapping("/update-product")
    public String updateProductPage() {
        return "update-product";
    }


}
