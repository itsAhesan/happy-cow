package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.ProductDTO;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.entity.ProductEntity;
import com.xworkz.happycow.repo.ProductRepo;
import com.xworkz.happycow.util.ExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
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

    @Autowired
    private ExcelHelper excelHelper;

    @Override
    public void exportAllProducts(OutputStream os) throws IOException {
        log.info("Exporting all products to Excel...");

        List<ProductEntity> entities = productRepo.findAllActiveProducts();
        if (entities == null || entities.isEmpty()) {
            log.warn("No products found to export.");
            return;
        }

        // Convert entities to DTOs (optional, depending on ExcelHelper)
        List<ProductDTO> dtos = new ArrayList<>();
        for (ProductEntity e : entities) {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(e.getProductId());
            dto.setProductName(e.getProductName());
            dto.setProductPrice(e.getProductPrice());
            dto.setProductType(e.getProductType());
            dto.setActive(e.getActive());
            dto.setCreatedBy(e.getCreatedBy());
            dto.setCreatedAt(e.getCreatedAt());
            dtos.add(dto);
        }

        excelHelper.productsToExcel(dtos, os);
        log.info("Export successful. Total records: {}", dtos.size());
    }


    @Override
    public List<String> importFromExcel(MultipartFile file, String adminName) throws IOException {
        log.info("Importing products from Excel file: {}", file.getOriginalFilename());
        List<String> summary = new ArrayList<>();

        List<ProductDTO> dtos = excelHelper.excelToProducts(file.getInputStream());
        if (dtos.isEmpty()) {
            summary.add("No valid product rows found in the uploaded file.");
            return summary;
        }

        List<ProductEntity> toSave = new ArrayList<>();
        int row = 1;
        for (ProductDTO dto : dtos) {
            // Basic validation
            if (dto.getProductName() == null || dto.getProductName().isEmpty()) {
                summary.add("Row " + row + ": skipped (missing product name)");
            } else if (dto.getProductPrice() == null || dto.getProductPrice() <= 0) {
                summary.add("Row " + row + ": skipped (invalid price)");
            } else {
                // Convert DTO -> Entity
                ProductEntity entity = new ProductEntity();
                entity.setProductName(dto.getProductName().trim());
                entity.setProductPrice(dto.getProductPrice());
                entity.setProductType(dto.getProductType() == null ? "Sell" : dto.getProductType());
                entity.setActive(true);
                entity.setCreatedBy(adminName);
                entity.setCreatedAt(LocalDateTime.now());
                toSave.add(entity);
            }
            row++;
        }

        if (!toSave.isEmpty()) {
            productRepo.saveAll(toSave);
            summary.add("✅ Imported " + toSave.size() + " valid products successfully.");
        } else {
            summary.add("⚠️ No valid products to import.");
        }

        log.info("Import completed. {} valid products added.", toSave.size());
        return summary;
    }








   /* @Override         //working
    public void exportAllProducts(OutputStream os) throws IOException {
        List<ProductDTO> all = getAllProducts(1, Integer.MAX_VALUE); // or use a repository method to get all
        excelHelper.productsToExcel(all, os);
    }

    @Override
    public List<String> importFromExcel(MultipartFile file, String adminName) throws IOException {
        List<ProductDTO> dtos = excelHelper.excelToProducts(file.getInputStream());
        List<String> summary = new ArrayList<>();
        if (dtos.isEmpty()) {
            summary.add("No valid product rows found in the file.");
            return summary;
        }

        // convert & save (batch)
        List<ProductEntity> entitiesToSave = new ArrayList<>();
        int rowNum = 1;
        for (ProductDTO dto : dtos) {
            // Additional validation if needed
            if (dto.getProductName() == null || dto.getProductName().isEmpty()) {
                summary.add("Row " + rowNum + ": skipped - product name missing.");
            } else if (dto.getProductPrice() == null) {
                summary.add("Row " + rowNum + ": skipped - product price missing/invalid.");
            } else {
                ProductEntity e = new ProductEntity();
                e.setProductName(dto.getProductName().trim());
                e.setProductPrice(dto.getProductPrice());
                e.setProductType(dto.getProductType() == null ? "Sell" : dto.getProductType());
                e.setCreatedBy(adminName);
                e.setCreatedAt(LocalDateTime.now());
                e.setActive(true);
                entitiesToSave.add(e);
            }
            rowNum++;
        }

        if (!entitiesToSave.isEmpty()) {
            productRepo.saveAll(entitiesToSave); // repository handles persist with EM
            summary.add("Saved " + entitiesToSave.size() + " products.");
        }

        return summary;
    }*/
}
