package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.dto.ProductCollectionAndAgentDTO;
import com.xworkz.happycow.dto.ProductCollectionDTO;
import com.xworkz.happycow.dto.ProductDTO;

import java.util.List;

public interface ProductCollectionService {
    List<ProductDTO> getAllProductsByTypesOfMilk();

    void saveProductCollection(ProductCollectionDTO dto, AdminDTO adminDTO);

    List<ProductCollectionDTO> getAllProductCollectionsByDate(String date);

    ProductCollectionAndAgentDTO getDetailsDTO(Integer id);
}
