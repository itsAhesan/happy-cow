package com.xworkz.happycow.repo;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.entity.AgentEntity;
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
public class AgentRepoImpl implements AgentRepo {
    @Override
    public AgentEntity findById(Integer id) {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();


            AgentEntity agentEntity = em.find(AgentEntity.class, id);

            return agentEntity;


        } catch (Exception e) {

            log.error("Failed to find AgentEntity by id: {}", id, e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Override
    public void save(AgentEntity agent) {

        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.persist(agent);
            et.commit();

        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to save AgentEntity: {}", agent, e);

        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Autowired
    private EntityManagerFactory emf;


    /*@Override
    public List<AgentEntity> findAll() {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();
            List<AgentEntity> list = em.createNamedQuery("findAllAgents", AgentEntity.class).getResultList();
            log.info("List of Agents: {}", list);
            return list;
        } catch (Exception e) {
            if (em != null) {
                em.close();
            }
            log.error("Failed to find all Agents", e);
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }*/
    @Override
    public List<AgentEntity> findAll(int pageNumber, int pageSize) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<AgentEntity> query = em.createNamedQuery("findAllAgents", AgentEntity.class);

            // Apply pagination
            query.setFirstResult((pageNumber - 1) * pageSize); // offset
            query.setMaxResults(pageSize); // limit

            List<AgentEntity> list = query.getResultList();
            log.info("Paged Agents: {}", list);
            return list;
        } catch (Exception e) {
            log.error("Failed to find all Agents", e);
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }



    @Override
    public boolean update(AgentEntity agentEntity) {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.merge(agentEntity);
            et.commit();
            return true;

        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to update AgentEntity: {}", agentEntity, e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Override
    public boolean delete(Integer id) {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            AgentEntity agent = em.find(AgentEntity.class, id);
                agent.setActive(false);
                em.merge(agent);  // update instead of delete
          //  em.remove(em.find(AgentEntity.class, id));
            et.commit();
            log.info("AgentEntity deleted successfully with id: {}", id);
            return true;
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to delete AgentEntity with id: {}", id, e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Override
    public AgentEntity findByEmail(String email) {

        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            AgentEntity agentEntity = em.createNamedQuery("findByAgentEmail", AgentEntity.class).setParameter("email", email).getSingleResult();

            return agentEntity;
        }catch (Exception e) {

            log.error("Failed to find AgentEntity by email: {}", email);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }



    }

    @Override
    public AgentEntity findByPhoneNumber(String phoneNumber) {

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            AgentEntity agentEntity = em.createNamedQuery("findByAgentPhoneNumber", AgentEntity.class).setParameter("phoneNumber", phoneNumber).getSingleResult();
            return agentEntity;
        }catch (Exception e) {
            log.error("Failed to find AgentEntity by phone number: {}", phoneNumber);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    public long countAgents() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery("SELECT COUNT(a) FROM AgentEntity a", Long.class).getSingleResult();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AgentEntity> searchAgents(String keyword, int offset, int size) {


        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            String query = "SELECT a FROM AgentEntity a " +
                    "WHERE lower(a.firstName) LIKE :kw " +
                    "OR lower(a.lastName) LIKE :kw " +
                    "OR lower(a.email) LIKE :kw " +
                    "OR a.phoneNumber LIKE :kw "+
                     "AND a.active = true";

            return em.createQuery(query, AgentEntity.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();



        } catch (Exception e) {
            log.error("Failed to search Agents", e);
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }






    }

    public long countAgentsBySearch(String keyword) {

        EntityManager em = null;
        try {
            em = emf.createEntityManager();




        String query = "SELECT COUNT(a) FROM AgentEntity a " +
                "WHERE lower(a.firstName) LIKE :kw " +
                "OR lower(a.lastName) LIKE :kw " +
                "OR lower(a.email) LIKE :kw " +
                "OR a.phoneNumber LIKE :kw " +
                "AND a.active = true";

        return em.createQuery(query, Long.class)
                .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                .getSingleResult();

        } catch (Exception e) {
            log.error("Failed to count Agents by search", e);
            return 0;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<String> getAllMilkTypes() {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

          //  String query = "SELECT DISTINCT a.productName FROM ProductEntity a where a.active = true and a.productType = 'Buy' and a.productName LIKE 'Milk' ";
            String query = "SELECT DISTINCT a.productName FROM ProductEntity a " +
                    "WHERE a.active = true " +
                    "AND a.productType = 'Buy' " +
                    "AND a.productName LIKE '%Milk%'";


            TypedQuery<String> typedQuery = em.createQuery(query, String.class);
            List<String> milkTypes = typedQuery.getResultList();
            return milkTypes;

        } catch (Exception e) {
            log.error("Failed to get all milk types", e);
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


}
