package com.election.backendjava.repository;

import com.election.backendjava.model.Region;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RegionJPARepository implements EntityRepository<Region> {

    @PersistenceContext
    protected EntityManager entityManager;

    public Region save(Region region) {
        return this.entityManager.merge(region);
    }

    public List<Region> findAll() {
        TypedQuery<Region> query = this.entityManager.createQuery(
                "SELECT r FROM Region r", Region.class
        );
        return query.getResultList();
    }

    public Region findById(long id) {
        return this.entityManager.find(Region.class, id);
    }

    public Region deleteById(long id) {
        Region region = findById(id);
        if (region != null) {
            this.entityManager.remove(region);
        }
        return region;
    }

    @Override
    public List<Region> findByQuery(String jpqlName, Object... params) {
        return List.of();
    }

    public Region findByLogicalNumber(String logicalNumber) {
        TypedQuery<Region> query = this.entityManager.createQuery(
                "SELECT r FROM Region r WHERE r.logicalNumber = :logicalNumber", Region.class
        );
        query.setParameter("logicalNumber", logicalNumber);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
