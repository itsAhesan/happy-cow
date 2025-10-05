package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts(int page, int size);

    long getProductCount();

    List<ProductDTO> searchProducts(String trim, int page, int size);

    long getProductSearchCount(String trim);

    void saveProduct(ProductDTO productDTO, String adminName);

    ProductDTO getProductById(Integer id);

    Boolean updateProduct(ProductDTO productDTO, String adminName);

    boolean deleteProduct(Integer id, String adminName);

    boolean existsByProductName(String productName);
}
