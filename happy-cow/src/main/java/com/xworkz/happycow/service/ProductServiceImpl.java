package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.ProductDTO;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.entity.ProductEntity;
import com.xworkz.happycow.repo.ProductRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Override
    public List<ProductDTO> getAllProducts(int page, int size) {
        List<ProductEntity> productEntities = productRepo.findAll(page, size);
        List<ProductDTO> productDTOs = new ArrayList<>();

        for (ProductEntity productEntity : productEntities) {
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(productEntity, productDTO);
            productDTOs.add(productDTO);
        }

        return productDTOs;


    }

    @Override
    public long getProductCount() {
        return productRepo.countProducts();
    }

    @Override
    public List<ProductDTO> searchProducts(String trim, int page, int size) {

       int offset = (page - 1) * size;
       List<ProductEntity> productEntities = productRepo.searchProducts(trim, offset, size);
       List<ProductDTO> productDTOs = new ArrayList<>();
       for (ProductEntity productEntity : productEntities) {
           ProductDTO productDTO = new ProductDTO();
           BeanUtils.copyProperties(productEntity, productDTO);
           productDTOs.add(productDTO);
       }
       return productDTOs;




    }

    @Override
    public long getProductSearchCount(String trim) {
        return productRepo.countProductsBySearch(trim);
    }

    @Override
    public void saveProduct(ProductDTO productDTO, String adminName) {

        ProductEntity productEntity = new ProductEntity();

        productEntity.setProductName(productDTO.getProductName());
        productEntity.setProductPrice(productDTO.getProductPrice());
        productEntity.setCreatedBy(adminName);
        productEntity.setCreatedAt(LocalDateTime.now());
        productEntity.setProductType(productDTO.getProductType());
    /*    productEntity.setUpdatedBy(adminName);
        productEntity.setUpdatedAt(LocalDateTime.now());*/

        productRepo.save(productEntity);


    }

    @Override
    public ProductDTO getProductById(Integer id) {

        ProductEntity productEntity = productRepo.findById(id);

        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(productEntity, productDTO);

        return productDTO;
    }

    @Override
    public Boolean updateProduct(ProductDTO productDTO, String adminName) {

        ProductEntity productEntity = productRepo.findById(productDTO.getProductId());

        if(productEntity == null) {
            return false;
        }

    //    ProductEntity productEntity = new ProductEntity();
      //  BeanUtils.copyProperties(productDTO, productEntity, "createdBy", "createdAt", "updatedBy", "updatedAt");

        productEntity.setProductName(productDTO.getProductName());
        productEntity.setProductPrice(productDTO.getProductPrice());
        productEntity.setProductType(productDTO.getProductType());

        productEntity.setUpdatedBy(adminName);
        productEntity.setUpdatedAt(LocalDateTime.now());

        Boolean updated = productRepo.update(productEntity);
        if (updated) {
            return true;
        } else {
            return false;
        }


    }

    @Override
    public boolean deleteProduct(Integer id, String adminName) {

        ProductEntity productEntity = productRepo.findById(id);

        if(productEntity == null) {
            return false;
        }else {
            productEntity.setActive(false);
            productEntity.setUpdatedBy(adminName);
            productEntity.setUpdatedAt(LocalDateTime.now());
            productRepo.delete(productEntity);
            return true;
        }


    }

    @Override
    public boolean existsByProductName(String productName) {

        ProductEntity productEntity = productRepo.findByProductName(productName);
        if(productEntity != null) {
            return true;
        }


        return false;
    }
}
