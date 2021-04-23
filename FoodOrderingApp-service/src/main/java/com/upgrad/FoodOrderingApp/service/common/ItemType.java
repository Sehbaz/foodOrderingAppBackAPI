package com.upgrad.FoodOrderingApp.service.common;


public enum ItemType {
    VEG("VEG"),

    NON_VEG("NON_VEG");

    private String value;

    ItemType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }


    public static ItemType fromValue(String text) {
        for (ItemType b : ItemType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}