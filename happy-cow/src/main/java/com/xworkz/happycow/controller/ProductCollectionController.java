package com.xworkz.happycow.controller;


import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.dto.ProductCollectionAndAgentDTO;
import com.xworkz.happycow.dto.ProductCollectionDTO;
import com.xworkz.happycow.dto.ProductDTO;
import com.xworkz.happycow.entity.ProductEntity;
import com.xworkz.happycow.service.ProductCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/")
@Slf4j
public class ProductCollectionController {

    @Autowired
    private ProductCollectionService productCollectionService;


    @GetMapping("productCollection")
    public String productCollection(HttpSession session, Model model) {
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {


            return "redirect:/adminLogin";
        }

        List<ProductDTO> products = productCollectionService.getAllProductsByTypesOfMilk();



        model.addAttribute("products",products);

        log.info("ProductCollectionController is working");
        log.info("products: {}",products);


        return "productCollection";
    }

    @PostMapping("/saveProductCollection")
    public String save(@ModelAttribute("productCollection") ProductCollectionDTO dto,
                       Model model,HttpSession session) {
       /* // 1) Enforce price from backend product (do NOT trust client price)
        ProductEntity product = productService.getActiveProductByName(dto.getTypeOfMilk());
        dto.setPrice(product.getProductPrice());*/

        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {

            return "redirect:/adminLogin";
        }

        log.info("ProductCollectionController save is working");
        log.info("dto: {}",dto);

        productCollectionService.saveProductCollection(dto, loggedInAdmin);



        model.addAttribute("successMessage", "Product collection saved.");
        return "redirect:/productCollection";
    }

    @GetMapping("productCollectionList")
    public String list(@RequestParam(required = false) String date,
                       Model model, HttpSession session) {

        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }

        List<ProductCollectionDTO> productCollections = productCollectionService.getAllProductCollectionsByDate(date);


       log.info("productCollections: {}",productCollections);
        model.addAttribute("collections", productCollections);


       // List<ProductCollectionEntity> collections = queryService.list(date);
        model.addAttribute("collections", productCollections);
        model.addAttribute("date", date == null ? "" : date);

        return "productCollectionList";
    }

    @GetMapping(value = "/productCollection/details", produces = "application/json")
    @ResponseBody
    public ResponseEntity<ProductCollectionAndAgentDTO> details(@RequestParam Integer id) {
        log.info("details method started");
        log.info("id: {}",id);
        try {
            return ResponseEntity.ok(productCollectionService.getDetailsDTO(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
