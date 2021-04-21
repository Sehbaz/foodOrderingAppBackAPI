package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signUpCustomer(CustomerEntity customerEntity)throws SignUpRestrictedException {
        CustomerEntity existingCustomerEntity = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());

        if(existingCustomerEntity != null){
            throw new SignUpRestrictedException("SGR-001" , "This contact number is already registered! Try other contact number");
        }

        String[] encryptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedPassword[0]);
        customerEntity.setPassword(encryptedPassword[1]);
        CustomerEntity createdCustomerEntity = customerDao.createCustomer(customerEntity);

        return createdCustomerEntity;

    }
}
