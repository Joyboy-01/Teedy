package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.UserRegistrationRequest;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserRegistrationRequestDao {
    public String create(UserRegistrationRequest req) throws Exception {
        req.setId(UUID.randomUUID().toString());
        req.setCreateDate(new Date());
        req.setStatus("pending");
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select r from UserRegistrationRequest r where r.username = :username and r.status = 'pending'");
        q.setParameter("username", req.getUsername());
        if (!q.getResultList().isEmpty()) {
            throw new Exception("AlreadyRequested");
        }
        em.persist(req);
        return req.getId();
    }

    public List<UserRegistrationRequest> findAllPending() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select r from UserRegistrationRequest r where r.status = 'pending'");
        return q.getResultList();
    }

    public UserRegistrationRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(UserRegistrationRequest.class, id);
    }

    public void update(UserRegistrationRequest req) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.merge(req);
    }

    public void delete(UserRegistrationRequest req) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.remove(em.contains(req) ? req : em.merge(req));
    }
}
