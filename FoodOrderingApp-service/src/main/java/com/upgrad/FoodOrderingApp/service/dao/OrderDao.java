package com.upgrad.FoodOrderingApp.service.dao;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;


    public OrdersEntity saveOrder(OrdersEntity ordersEntity){
        entityManager.persist(ordersEntity);
        return ordersEntity;
    }

}