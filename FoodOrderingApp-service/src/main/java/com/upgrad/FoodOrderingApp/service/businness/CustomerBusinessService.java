package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import java.time.ZonedDateTime;

@Service
public class CustomerBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    UitilityProvider uitilityProvider;

    @Autowired
    CustomerAuthDao customerAuthDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signUpCustomer(CustomerEntity customerEntity)throws SignUpRestrictedException {
        CustomerEntity existingCustomerEntity = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());

        if (existingCustomerEntity != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
        }
        if (customerEntity.getContactNumber() == null || customerEntity.getEmail() == null || customerEntity.getFirstName() == null || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        if (!uitilityProvider.isEmailValid(customerEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if (!uitilityProvider.isContactValid(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        if (!uitilityProvider.isValidPassword(customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        String[] encryptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedPassword[0]);
        customerEntity.setPassword(encryptedPassword[1]);
        CustomerEntity createdCustomerEntity = customerDao.createCustomer(customerEntity);

        return createdCustomerEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticateCustomer(String contactNumber, String password)throws AuthorizationFailedException {
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactNumber);
        if (customerEntity == null) {
            throw new AuthorizationFailedException("ATH-001", "This contact number has not been registered!");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomer(customerEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthEntity.setLoginAt(now);
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setUuid(UUID.randomUUID().toString());

            CustomerAuthEntity createdCustomerAuthEntity = customerAuthDao.createCustomerAuth(customerAuthEntity);
            return createdCustomerAuthEntity;
        }else {
            throw new AuthorizationFailedException("ATH-002", "Invalid Credentials");
        }
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity customerLogout(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }


        customerAuthEntity.setLogoutAt(ZonedDateTime.now());

        CustomerAuthEntity upatedCustomerAuthEntity = customerAuthDao.customerLogout(customerAuthEntity);
        return upatedCustomerAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
        public CustomerEntity updateCustomerDetails(CustomerEntity customerEntity, String accessToken) throws AuthorizationFailedException, UpdateCustomerException {
            CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

            if (customerAuthEntity == null) {
                throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
            }

            if (customerAuthEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
            }

            final ZonedDateTime now = ZonedDateTime.now();

            if (customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
            }

            if (customerEntity.getFirstName() == null) {
                throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
            }

            CustomerEntity customerToBeUpdated = customerDao.getCustomerByUuid(customerAuthEntity.getCustomer().getUuid());

            customerToBeUpdated.setFirstName(customerEntity.getFirstName());
            customerToBeUpdated.setLastName(customerEntity.getLastName());

            CustomerEntity updatedCustomer = customerDao.updateCustomer(customerToBeUpdated);

            return updatedCustomer;
        }
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(String accessToken, String oldPassword, String newPassword)throws AuthorizationFailedException,UpdateCustomerException{
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

        if(customerAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        }

        if(customerAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if(customerAuthEntity.getExpiresAt().compareTo(now) < 0){
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }

        if(!uitilityProvider.isValidPassword(newPassword)){
            throw new UpdateCustomerException("UCR-001","Weak password!");
        }

        String encryptedOldPassword = passwordCryptographyProvider.encrypt(oldPassword, customerAuthEntity.getCustomer().getSalt());

        if(encryptedOldPassword.equals(customerAuthEntity.getCustomer().getPassword())){
            CustomerEntity customerEntity = customerDao.getCustomerByUuid(customerAuthEntity.getCustomer().getUuid());

            String[] encryptedPassword = passwordCryptographyProvider.encrypt(newPassword);
            customerEntity.setSalt(encryptedPassword[0]);
            customerEntity.setPassword(encryptedPassword[1]);

            CustomerEntity updatedCustomerEntity = customerDao.updateCustomer(customerEntity);

            return updatedCustomerEntity;

        }else{
            throw new UpdateCustomerException("UCR-004","Incorrect old password!");
        }

    }
    /* This method is to getCustomer using accessToken and return the CustomerEntity .
    If error throws exception with error code and error message.
    */
    public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

        if (customerAuthEntity == null) {//Checking if Customer not logged In
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {//Checking if cutomer is logged Out
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) <= 0) {//Checking accessToken is Expired.
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuthEntity.getCustomer();
    }


}
