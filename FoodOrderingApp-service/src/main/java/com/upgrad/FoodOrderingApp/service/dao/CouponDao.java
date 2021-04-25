package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByCouponId(String couponUuid) {
        try {
            CouponEntity couponEntity = entityManager.createNamedQuery("getCouponByCouponId",CouponEntity.class).setParameter("uuid",couponUuid).getSingleResult();
            return couponEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public CouponEntity getCouponByCouponName(String couponName){
        try{
            CouponEntity couponEntity = entityManager.createNamedQuery("getCouponByCouponName",CouponEntity.class).setParameter("coupon_name",couponName).getSingleResult();
            return couponEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}