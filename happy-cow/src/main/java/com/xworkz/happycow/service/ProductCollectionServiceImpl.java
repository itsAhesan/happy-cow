package com.xworkz.happycow.service;


import com.xworkz.happycow.dto.*;
import com.xworkz.happycow.entity.*;
import com.xworkz.happycow.repo.AdminRepo;
import com.xworkz.happycow.repo.AgentRepo;
import com.xworkz.happycow.repo.ProductCollectionRepo;
import com.xworkz.happycow.repo.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ProductCollectionServiceImpl implements ProductCollectionService{


    @Autowired
    private ProductRepo productRepo;


    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private ProductCollectionRepo productCollectionRepo;



    @Override
    public List<ProductDTO> getAllProductsByTypesOfMilk() {

        List<ProductEntity> products = productRepo.getAllProductsByTypesOfMilk();

        List<ProductDTO> productDTOs = new ArrayList<>();
        for (ProductEntity productEntity : products) {
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(productEntity, productDTO);
            productDTOs.add(productDTO);
        }


        return productDTOs;
    }

    @Override
    public void saveProductCollection(ProductCollectionDTO dto, AdminDTO adminDTO) {

        AdminEntity adminEntity = adminRepo.findByEmail(adminDTO.getEmailId());

        AgentEntity agentEntity = agentRepo.findByPhoneNumber(dto.getPhoneNumber());


        // --- Build entity
        ProductCollectionEntity entity = new ProductCollectionEntity();
        entity.setAgent(agentEntity);
        entity.setAdmin(adminEntity);
        entity.setTypeOfMilk(dto.getTypeOfMilk());  // store the canonical product name
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setCollectedAt(LocalDate.now());

        productCollectionRepo.save(entity);

        ProductCollectionAuditEntity audit = new ProductCollectionAuditEntity();
        audit.setCreatedAt(LocalDateTime.now());
        audit.setCreatedBy(adminEntity.getAdminName()); // or admin.getEmailId()
        audit.setProductCollectionEntity(entity);

        // link back (not owning side, but keeps model consistent)
        entity.setProductCollectionAuditEntity(audit);

        // persist audit
        productCollectionRepo.saveAudit(audit);


    }

   /* @Override
    public List<ProductCollectionDTO> getAllProductCollectionsByDate(String date) {



            if (date == null) {

                List<ProductCollectionEntity> collections = productCollectionRepo.findAllOrdered();
                List<ProductCollectionDTO> productCollectionDTOs = new ArrayList<>();
                for (ProductCollectionEntity collection : collections) {
                    ProductCollectionDTO dto = new ProductCollectionDTO();
                    BeanUtils.copyProperties(collection, dto);
                    productCollectionDTOs.add(dto);
                }
                return productCollectionDTOs;




              //  return productCollectionRepo.findAllOrdered();
            }
            LocalDate d = LocalDate.parse(date);
         //   return productCollectionRepo.findByCollectedDate(d);



        return Collections.emptyList();
    }*/

    @Override
    public List<ProductCollectionDTO> getAllProductCollectionsByDate(String date) {
        List<ProductCollectionEntity> collections;

        if (date == null ) {
            collections = productCollectionRepo.findAllOrdered();
            log.info("collections: {}",collections);
        } else {
            LocalDate d = LocalDate.parse(date);
            collections = productCollectionRepo.findByCollectedDate(d);
        }

        List<ProductCollectionDTO> list = new ArrayList<>();
        for (ProductCollectionEntity pc : collections) {
            ProductCollectionDTO dto = new ProductCollectionDTO();
            // copy simple fields
            dto.setProductCollectionId(pc.getProductCollectionId());
            dto.setTypeOfMilk(pc.getTypeOfMilk());
            dto.setPrice(pc.getPrice());
            dto.setQuantity(pc.getQuantity());
            dto.setTotalAmount(pc.getTotalAmount());
            dto.setCollectedAt(pc.getCollectedAt());

            // map AGENT -> AgentDTO (so JSP can show name/phone/email/address)
            if (pc.getAgent() != null) {
                AgentDTO a = new AgentDTO();
                a.setAgentId(pc.getAgent().getAgentId());
                a.setFirstName(pc.getAgent().getFirstName());
                a.setLastName(pc.getAgent().getLastName());
                a.setEmail(pc.getAgent().getEmail());
                a.setPhoneNumber(pc.getAgent().getPhoneNumber());
                a.setAddress(pc.getAgent().getAddress());
                dto.setAgent(a);

                // also keep phoneNumber in DTO if your JSP uses dto.phoneNumber
                dto.setPhoneNumber(pc.getAgent().getPhoneNumber());
            }

            // map ADMIN -> AdminDTO (for navbar or if you want to show who recorded it)
            if (pc.getAdmin() != null) {
                AdminDTO ad = new AdminDTO();
                ad.setAdminId(pc.getAdmin().getAdminId());
                ad.setAdminName(pc.getAdmin().getAdminName());
                ad.setEmailId(pc.getAdmin().getEmailId());
                ad.setPhoneNumber(pc.getAdmin().getPhoneNumber());
                dto.setAdmin(ad);
            }

            list.add(dto);
        }
        return list;
    }

   /* @Override
    public ProductCollectionAndAgentDTO getDetailsDTO(Integer id) {

        ProductCollectionEntity pc = productCollectionRepo.findById(id);
        log.info("pc: {}",pc);

        AgentEntity a = pc.getAgent();
        if (a != null) Hibernate.initialize(a);

        ProductCollectionAndAgentDTO dto = new ProductCollectionAndAgentDTO();
        dto.setProductCollectionId(pc.getProductCollectionId());
        // Agent
       // dto.setAgentName(a.getFirstName() + " " + a.getLastName());
        dto.setAgentName(a.getFirstName() + " " +a.getLastName());
        dto.setAgentEmail((a.getEmail()));
        dto.setAgentPhone((a.getPhoneNumber()));
        dto.setAgentAddress((a.getAddress()));
        // Collection
        dto.setTypeOfMilk(pc.getTypeOfMilk());
        dto.setPrice(pc.getPrice());
        dto.setQuantity(pc.getQuantity());
        dto.setTotalAmount(pc.getTotalAmount());

        log.info("dto: {}",dto);

        return dto;

    }*/

    @Override
    public ProductCollectionAndAgentDTO getDetailsDTO(Integer id) {
        // Make sure repo method uses the entity-graph version
        ProductCollectionEntity pc = productCollectionRepo.findByIdWithRelations(id);
        log.info("pc id: {}", pc.getProductCollectionId()); // safe log

        AgentEntity a = pc.getAgent(); // already loaded by graph

        ProductCollectionAndAgentDTO dto = new ProductCollectionAndAgentDTO();
        dto.setProductCollectionId(pc.getProductCollectionId());

        // Agent
        String first = a != null && a.getFirstName() != null ? a.getFirstName() : "";
        String last  = a != null && a.getLastName()  != null ? a.getLastName()  : "";
        dto.setAgentName((first + " " + last).trim());
        dto.setAgentEmail(a != null ? nullToEmpty(a.getEmail())       : "");
        dto.setAgentPhone(a != null ? nullToEmpty(a.getPhoneNumber()) : "");
        dto.setAgentAddress(a != null ? nullToEmpty(a.getAddress())   : "");

        // Collection
        dto.setTypeOfMilk(pc.getTypeOfMilk());
        dto.setPrice(pc.getPrice());
        dto.setQuantity(pc.getQuantity());
        dto.setTotalAmount(pc.getTotalAmount());

        log.info("dto built for collection {}", dto.getProductCollectionId());
        return dto;
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }



}
