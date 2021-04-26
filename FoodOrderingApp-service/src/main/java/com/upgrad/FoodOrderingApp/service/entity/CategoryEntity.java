package com.upgrad.FoodOrderingApp.service.entity;


import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "category",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries({

        @NamedQuery(name = "getCategoryByUuid",query = "SELECT c FROM CategoryEntity c WHERE c.uuid = :uuid"),
        @NamedQuery(name = "getAllCategoriesOrderedByName",query = "SELECT c FROM CategoryEntity c ORDER BY c.categoryName ASC "),
})
public class CategoryEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "category_name")
    @Size(max = 255)
    private String categoryName;


    @Transient
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<ItemEntity> items;

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> itemEntities) {
        this.items = itemEntities;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}