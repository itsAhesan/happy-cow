package com.xworkz.happycow.repo;

import com.xworkz.happycow.dto.ProductDTO;
import com.xworkz.happycow.entity.ProductEntity;

import java.util.List;

public interface ProductRepo {
    List<ProductEntity> findAll(int page, int size);

    long countProducts();

    void save(ProductEntity productEntity);

    ProductEntity findById(Integer id);

    Boolean update(ProductEntity productEntity);

    void delete(ProductEntity productEntity);

    List<ProductEntity> searchProducts(String trim, int offset, int size);

    long countProductsBySearch(String trim);

    ProductEntity findByProductName(String productName);

    List<ProductEntity> getAllProductsByTypesOfMilk();
}
