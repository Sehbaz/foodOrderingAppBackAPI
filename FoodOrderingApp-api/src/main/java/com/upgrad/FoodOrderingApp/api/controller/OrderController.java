package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderResponse;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    OrderService orderService;

    @Autowired
    CustomerService customerService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    AddressService addressService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    ItemService itemService;

    @RequestMapping(method = RequestMethod.GET,path = "/coupon/{coupon_name}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader(value = "authorization") final String authorization, @PathVariable(value = "coupon_name")final String couponName) throws AuthorizationFailedException, CouponNotFoundException {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .couponName(couponEntity.getCouponName())
                .id(UUID.fromString(couponEntity.getUuid()))
                .percent(couponEntity.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST,path = "",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader(value = "authorization")final String authorization, final SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, PaymentMethodNotFoundException, AddressNotFoundException, RestaurantNotFoundException, CouponNotFoundException ,ItemNotFoundException{

        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];

        //Calls customerService getCustomerMethod to check the validity of the customer.this methods returns the customerEntity.
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        //Calls orderService getCouponByCouponId method to get the CouponEntity by it uuid.
        CouponEntity couponEntity = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());


        //Calls paymentService getPaymentByUUID method to get the PaymentEntity by it uuid.
        PaymentEntity paymentEntity = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());

        //Calls addressService getAddressByUUID method to get the AddressEntity by it uuid.
        AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId(),customerEntity);

        //Calls restaurantService restaurantByUUID method to get the RestaurantEntity by it uuid.
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());

        //Creating new order entity from the details fetched earlier and request details received.

        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setUuid(UUID.randomUUID().toString());
        ordersEntity.setBill(saveOrderRequest.getBill().floatValue());
        ordersEntity.setDate(ZonedDateTime.now());
        ordersEntity.setCustomer(customerEntity);
        ordersEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        ordersEntity.setPayment(paymentEntity);
        ordersEntity.setAddress(addressEntity);
        ordersEntity.setRestaurant(restaurantEntity);
        ordersEntity.setCoupon(couponEntity);

        //Calls orderService saveOrder method to persist the order in database.
        OrdersEntity savedOrderEntity = orderService.saveOrder(ordersEntity);

        //Setting items for the OrderItemEntity
        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        for(ItemQuantity itemQuantity : itemQuantities) {

            OrderItemEntity orderItemEntity = new OrderItemEntity();

            ItemEntity itemEntity = itemService.getItemByUUID(itemQuantity.getItemId().toString());

            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setOrder(ordersEntity);
            orderItemEntity.setPrice(itemQuantity.getPrice());
            orderItemEntity.setQuantity(itemQuantity.getQuantity());

            //calls orderService saveOrderItem to persist the orderItemEntity cretaed
            OrderItemEntity savedOrderItem = orderService.saveOrderItem(orderItemEntity);
        }

        //Creating the SaveOrderResponse for the endpoint containing UUID and success message.
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse()
                .id(savedOrderEntity.getUuid())
                .status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse,HttpStatus.CREATED);
    }


}