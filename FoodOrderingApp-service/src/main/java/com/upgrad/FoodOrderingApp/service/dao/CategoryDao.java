package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;


    public CategoryEntity getCategoryByUuid(String uuid) {
        try {
            CategoryEntity categoryEntity = entityManager.createNamedQuery("getCategoryByUuid",CategoryEntity.class).setParameter("uuid",uuid).getSingleResult();
            return categoryEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}
