package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UitilityProvider {

    public boolean isValidPassword(String password){
        Boolean lowerCase = false;
        Boolean upperCase = false;
        Boolean number = false;
        Boolean specialCharacter = false;

        if(password.length() < 8){
            return false;
        }

        if(password.matches("(?=.*[0-9]).*")){
            number = true;
        }

        if(password.matches("(?=.*[a-z]).*")){
            lowerCase = true;
        }
        if(password.matches("(?=.*[A-Z]).*")){
            upperCase = true;
        }
        if(password.matches("(?=.*[#@$%&*!^]).*")){
            specialCharacter = true;
        }

        if(lowerCase && upperCase){
            if(specialCharacter && number){
                return true;
            }
        }else{
            return false;
        }
        return false;
    }

    public boolean isContactValid(String contactNumber){
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    public boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public boolean isPincodeValid(String pincode){
        Pattern p = Pattern.compile("\\d{6}\\b");
        Matcher m = p.matcher(pincode);
        return (m.find() && m.group().equals(pincode));
    }
    public  Boolean isValidRestaurantName(String restaurantName)throws RestaurantNotFoundException {
        if(restaurantName == null || restaurantName ==""){
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");
        }
        return true;
    }

    public boolean isValidCustomerRating(String cutomerRating){
        if(cutomerRating.equals("5.0")){
            return true;
        }
        Pattern p = Pattern.compile("[1-4].[0-9]");
        Matcher m = p.matcher(cutomerRating);
        return (m.find() && m.group().equals(cutomerRating));
    }
}
