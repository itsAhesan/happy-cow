package com.xworkz.happycow.restcontroller;


import com.xworkz.happycow.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {



    @Autowired
    private ProductService productService;

   /* @GetMapping("/checkProductName")
    public boolean checkProductName(@RequestParam String productName) {
        return !productService.existsByProductName(productName);
    }*/


    // Example Spring Controller

    @GetMapping("/registerProduct/checkProductName")
    public boolean checkProductName(@RequestParam("productName") String productName) {

        return !productService.existsByProductName(productName);
      /*  boolean exists = productService.existsByProductName(productName);
        return exists ? "true" : "false"; // "true" = valid for jQuery, "false" = invalid*/
    }




}
