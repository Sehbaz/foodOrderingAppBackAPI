package com.upgrad.FoodOrderingApp.api.controller;



import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET,path = "",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {
        List<CategoryEntity> categoryEntities = categoryService.getAllCategoriesOrderedByName();
        if (!categoryEntities.isEmpty()) {
            List<CategoryListResponse> categoryListResponses = new LinkedList<>();
            categoryEntities.forEach(categoryEntity -> {
                CategoryListResponse categoryListResponse = new CategoryListResponse()
                        .id(UUID.fromString(categoryEntity.getUuid()))
                        .categoryName(categoryEntity.getCategoryName());
                categoryListResponses.add(categoryListResponse);
            });

            CategoriesListResponse categoriesListResponse = new CategoriesListResponse().categories(categoryListResponses);
            return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<CategoriesListResponse>(new CategoriesListResponse(), HttpStatus.OK);
        }
    }


}