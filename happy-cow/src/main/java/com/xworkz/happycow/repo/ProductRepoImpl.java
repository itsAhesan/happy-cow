package com.xworkz.happycow.repo;

import com.xworkz.happycow.dto.ProductDTO;
import com.xworkz.happycow.entity.ProductEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class ProductRepoImpl implements ProductRepo {


    @Override
    public List<ProductEntity> getAllProductsByTypesOfMilk() {
        EntityManager em = null;
      try {
          em = emf.createEntityManager();


          String query = "SELECT DISTINCT a FROM ProductEntity a " +
                  "WHERE a.active = true " +
                  "AND a.productType = 'Buy' " +
                  "AND a.productName LIKE '%Milk%'";

          TypedQuery<ProductEntity> typedQuery = em.createQuery(query, ProductEntity.class);
          List<ProductEntity> findAllProductsByTypesOfMilk = typedQuery.getResultList();
          return findAllProductsByTypesOfMilk;


        //  List<ProductEntity> findAllProductsByTypesOfMilk = em.createNamedQuery(query, ProductEntity.class).getResultList();



        //  return findAllProductsByTypesOfMilk;
        } catch (Exception e) {
            log.error("Failed to find all Products", e);
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }

        }



    }

    @Autowired
    private EntityManagerFactory emf;


    @Override
    public List<ProductEntity> findAll(int page, int size) {

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<ProductEntity> query = em.createNamedQuery("findAllProducts", ProductEntity.class);

            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);

            List<ProductEntity> list = query.getResultList();
            log.info("Paged Products: {}", list);
            return list;
        } catch (Exception e) {
            log.error("Failed to find all Products", e);
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }

        }

    }

    @Override
    public long countProducts() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery("SELECT COUNT(p) FROM ProductEntity p", Long.class).getSingleResult();
        } catch (Exception e) {
            log.error("Failed to count Products", e);
            return 0;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Override
    public void save(ProductEntity productEntity) {

        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.persist(productEntity);
            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to save ProductEntity: {}", productEntity, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    @Override
    public ProductEntity findById(Integer id) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.find(ProductEntity.class, id);

        } catch (Exception e) {
            log.error("Failed to find ProductEntity by id: {}", id, e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Boolean update(ProductEntity productEntity) {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.merge(productEntity);
            et.commit();
            return true;
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }

            log.error("Failed to update ProductEntity: {}", productEntity, e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void delete(ProductEntity productEntity) {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.merge(productEntity);
            et.commit();

        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to delete ProductEntity: {}", productEntity, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    @Override
    public List<ProductEntity> searchProducts(String trim, int offset, int size) {
        EntityManager em=null;

        try {
            em = emf.createEntityManager();
          //  TypedQuery<ProductEntity> query = em.createNamedQuery("searchProducts", ProductEntity.class);
          /*  query.setFirstResult(offset);
            query.setMaxResults(size);
            return query.getResultList();*/

            String query = "SELECT p FROM ProductEntity p " +
                    "WHERE lower(p.productName) LIKE :trim " +
                    "OR lower(p.productType) LIKE :trim " +
                    "AND p.active = true";
            return em.createQuery(query, ProductEntity.class)
                    .setParameter("trim", "%" + trim.toLowerCase() + "%")
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();



        } catch (Exception e) {
            log.error("Failed to search Products", e);
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    @Override
    public long countProductsBySearch(String trim) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            String query = "SELECT COUNT(p) FROM ProductEntity p " +
                    "WHERE lower(p.productName) LIKE :trim " +
                    "OR lower(p.productType) LIKE :trim " +
                    "AND p.active = true";
            return em.createQuery(query, Long.class)
                    .setParameter("trim", "%" + trim.toLowerCase() + "%")
                    .getSingleResult();
        }catch (Exception e) {
            log.error("Failed to count Products by search", e);
            return 0;
        }finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Override
    public ProductEntity findByProductName(String productName) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            return em.createNamedQuery("findByProductName", ProductEntity.class)
            .setParameter("productName", productName)
            .getSingleResult();


        }catch (Exception e) {
            log.error("Failed to find ProductEntity by productName: {}", productName);
            return null;
        }finally {
            if (em != null) {
                em.close();
            }
        }

    }
}
