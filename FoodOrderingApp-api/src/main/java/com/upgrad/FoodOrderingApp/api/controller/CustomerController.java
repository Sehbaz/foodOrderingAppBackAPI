package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerBusinessService customerBusinessService;


// Sign up functionality

    @RequestMapping(method = RequestMethod.POST,path = "",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUpCustomer( final SignupCustomerRequest signupCustomerRequest)throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setUuid(UUID.randomUUID().toString());


        CustomerEntity signedUpCustomer =  customerBusinessService.signUpCustomer(customerEntity);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(signedUpCustomer.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse,HttpStatus.CREATED);
    }

    //Login Functionality

    @RequestMapping(method = RequestMethod.POST,path = "/login",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> customerLogin (@RequestHeader("authorization") final String authorization)throws AuthenticationFailedException {
        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuth = new String(decoded);
        String[] decodedArray = decodedAuth.split(":");

        CustomerAuthEntity customerAuthEntity = customerBusinessService.authenticateCustomer(decodedArray[0],decodedArray[1]);

        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", customerAuthEntity.getAccessToken());

        LoginResponse loginResponse = new LoginResponse()
                .id(customerAuthEntity.getCustomer().getUuid())
                .contactNumber(customerAuthEntity.getCustomer().getContactNumber())
                .emailAddress(customerAuthEntity.getCustomer().getEmail())
                .firstName(customerAuthEntity.getCustomer().getFirstName())
                .lastName(customerAuthEntity.getCustomer().getLastName())
                .message("LOGGED IN SUCCESSFULLY");

        return new ResponseEntity<LoginResponse>(loginResponse,headers,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST,path = "/logout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> customerLogout (@RequestHeader("authorization")final String authorization)throws AuthenticationFailedException{
        CustomerAuthEntity customerAuthEntity =  customerBusinessService.customerLogout(authorization);

        LogoutResponse logoutResponse = new LogoutResponse()
                .id(customerAuthEntity.getCustomer().getUuid())
                .message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse,HttpStatus.OK);
    }

}
