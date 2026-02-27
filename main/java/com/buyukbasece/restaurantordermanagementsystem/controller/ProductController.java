package com.buyukbasece.restaurantordermanagementsystem.controller;

import com.buyukbasece.restaurantordermanagementsystem.entity.Product;
import com.buyukbasece.restaurantordermanagementsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class ProductController {

    private final ProductService productService;


    @Autowired
    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @GetMapping("/menu")
    public String showMenu(Model model){
        List<Product> products =productService.getAllProducts();
        model.addAttribute("products", products);
        return "menu";

    }
    @GetMapping("/admin")
    public String adminPanel() {
        return "admin";
    }



    @GetMapping("/admin/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @GetMapping("/waiter")
    public String showWaiterPanel(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "waiter";
    }


    @PostMapping("/admin/add")
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/menu";
    }





}
