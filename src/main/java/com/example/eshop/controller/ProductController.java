package com.example.eshop.controller;

import com.example.eshop.entity.Product;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<Product> productPage = productService.getProducts(PageRequest.of(page, 10));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    @PostMapping
    public String createProduct(@ModelAttribute Product product,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        productService.saveProduct(product, imageFile);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        product.setId(id);
        productService.saveProduct(product, imageFile);
        return "redirect:/products";
    }

    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}