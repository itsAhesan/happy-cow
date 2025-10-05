package com.xworkz.happycow.controller;


import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.dto.ProductDTO;
import com.xworkz.happycow.entity.ProductEntity;
import com.xworkz.happycow.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;



/*    @GetMapping("productDashboard")
    public String productDashboard(HttpSession session) {
        log.info("productDashboard is working");

        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }
        return "productDashboard";
    }*/

    @GetMapping("productDashboard")
    public String productDashboard(HttpSession session, Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "8") int size,
                                   @RequestParam(required = false) String search) {

        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }

        List<ProductDTO> products;
        long totalProducts;

        if (search != null && !search.trim().isEmpty()) {
            // 🔍 Search filter applied
            products = productService.searchProducts(search.trim(), page, size);
            totalProducts = productService.getProductSearchCount(search.trim());
            model.addAttribute("search", search);
        } else {
            // 📄 Normal pagination
            products = productService.getAllProducts(page, size);
            totalProducts = productService.getProductCount();
        }

        int totalPages = (int) Math.ceil((double) totalProducts / size);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalRecords", totalProducts);

        return "productDashboard";
    }



    @PostMapping("saveProduct")
    public String saveProduct(@ModelAttribute ProductDTO productDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("saveProduct is working");
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }

        productService.saveProduct(productDTO, loggedInAdmin.getAdminName());
        redirectAttributes.addFlashAttribute("successMessage", "Product saved successfully");
        return "redirect:/productDashboard";

    }

    @GetMapping("editProduct")
    public String editProduct(@RequestParam("id") Integer id, Model model, HttpSession session) {
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }

        ProductDTO productDTO = productService.getProductById(id);
        model.addAttribute("product", productDTO);
        return "editProduct";
    }

    @PostMapping("updateProduct")
    public String updateProduct(@ModelAttribute ProductDTO productDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("updateProduct is working");
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }

        Boolean updated = productService.updateProduct(productDTO, loggedInAdmin.getAdminName());

        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update product");
        }

        return "redirect:/productDashboard";
    }

    @GetMapping("deleteProduct")
    public String deleteProduct(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes, HttpSession session) {
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }
        boolean deleted = productService.deleteProduct(id, loggedInAdmin.getAdminName());

        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete product!");
        }

        return "redirect:/productDashboard";
    }




    @GetMapping("registerProduct")
    public String showRegisterForm(HttpSession session, Model model) {

        log.info("registerProduct is working");

        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }
        model.addAttribute("productEntity", new ProductEntity());
         return "registerProduct"; // JSP page name
    }

  /*  @GetMapping("registerProduct")
    public String registerProduct(HttpSession session) {
        log.info("registerProduct is working");

        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }
        return "registerProduct";
    }*/


}
