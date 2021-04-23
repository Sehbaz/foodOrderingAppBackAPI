package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemDao itemDao;

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    RestaurantItemDao restaurantItemDao;

    @Autowired
    CategoryItemDao categoryItemDao;

    @Autowired
    CategoryDao categoryDao;


    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);

        List<RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemsByRestaurant(restaurantEntity);
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);
        List<ItemEntity> itemEntities = new LinkedList<>();

        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if(restaurantItemEntity.getItem().equals(categoryItemEntity.getItem())){
                    itemEntities.add(restaurantItemEntity.getItem());
                }
            });
        });

        return itemEntities;
    }

    public List<ItemEntity> getItemsByCategory(CategoryEntity categoryEntity) {
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);
        List<ItemEntity> itemEntities = new LinkedList<>();
        categoryItemEntities.forEach(categoryItemEntity -> {
            ItemEntity itemEntity = categoryItemEntity.getItem();
            itemEntities.add(itemEntity);
        });
        return itemEntities;
    }
}
