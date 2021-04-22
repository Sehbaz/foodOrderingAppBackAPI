package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {


    @PersistenceContext
    private EntityManager entityManager;


    public CustomerEntity getCustomerByContactNumber (final String contact_number){
        try{
            CustomerEntity customer = entityManager.createNamedQuery("customerByContactNumber",CustomerEntity.class).setParameter("contact_number",contact_number).getSingleResult();
            return customer;
        }catch (NoResultException nre){
            return null;
        }
    }


    public CustomerEntity createCustomer(CustomerEntity customerEntity){
        entityManager.persist(customerEntity);
        return customerEntity;
    }
    public CustomerEntity getCustomerByUuid (final String uuid){
        try {
            CustomerEntity customer = entityManager.createNamedQuery("customerByUuid",CustomerEntity.class).setParameter("uuid",uuid).getSingleResult();
            return customer;
        }catch (NoResultException nre){
            return null;
        }
    }
    public CustomerEntity updateCustomer(CustomerEntity customerToBeUpdated){
        entityManager.merge(customerToBeUpdated);
        return customerToBeUpdated;
    }
}