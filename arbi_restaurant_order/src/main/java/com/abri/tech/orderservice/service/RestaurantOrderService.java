package com.abri.tech.orderservice.service;

import com.abri.tech.orderservice.dto.RestaurantOrder;
import com.abri.tech.orderservice.entity.Order;
import com.abri.tech.orderservice.repo.RestaurantOrderRepo;
import com.abri.tech.orderservice.response.OrderDetailsResponse;
import com.abri.tech.orderservice.response.OrderResponse;
import com.abri.tech.orderservice.response.RestaurantResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Setter
@Getter
@Service
@Slf4j
public class RestaurantOrderService {

    private RestaurantOrderRepo restaurantOrderRepo;

    public OrderResponse saveOrder(RestaurantOrder restaurantOrder) {

        var order = new Order();
        BeanUtils.copyProperties(restaurantOrder, order);
        var savedOrder = restaurantOrderRepo.save(order);

        var orderResponse = new OrderResponse();
        orderResponse.setOrderId(savedOrder.getId());
        orderResponse.setOrderDetails("Hi " + restaurantOrder.getCustomerName().toUpperCase() +
                ", your order for " + savedOrder.getMenuName() + " will be delivered in 30 minutes");
        return orderResponse;
    }
/*
    public RestaurantOrder saveOrder(RestaurantOrder restaurantOrder) {
        var order = new Order();
        order.setCustomerName(restaurantOrder.getCustomerName());
        order.setMenuName(restaurantOrder.getMenuName());
        var saveOrder:Order= restaurantOrderRepo.save(order);
    }

*/

    public RestaurantResponse getAllOrders() {
        var allOrders = restaurantOrderRepo.findAll();
        var msg = allOrders.size() > 0 ?
                "Successfully retrieve order details , total order : " + allOrders.size() :
                "No order found ";
        var resResponse = buildResponse(allOrders, msg);
        return resResponse;

    }

    public RestaurantResponse getOrderForCustomer(String customerName) {
        var allOrders = restaurantOrderRepo.findOrderByCustomerName(customerName);
        var msg = allOrders.size() > 0 ?
                "Successfully retrieve order details , total order : " + allOrders.size() :
                "No order found for the customer :" + customerName;
        var resResponse = buildResponse(allOrders, msg);
        return resResponse;
    }

    public RestaurantResponse modifyOrder(RestaurantOrder restaurantOrder) {

        var orderOptional = restaurantOrderRepo.findById(Long.valueOf(restaurantOrder.getOrderId()));
        if (orderOptional.isEmpty()) {
            log.info("No order found for order is {} ", restaurantOrder.getOrderId());
            return RestaurantResponse.builder()
                    .message("No order found for orderId : " + restaurantOrder.getOrderId())
                    .build();
        }
        var order = orderOptional.get();
        order.setMenuName(restaurantOrder.getMenuName());
        var savedOrder = restaurantOrderRepo.save(order);

        var orderResponse = new OrderResponse();
        orderResponse.setOrderId(savedOrder.getId());
        orderResponse.setOrderDetails("Hi " + restaurantOrder.getCustomerName().toUpperCase() +
                ", your order for " + savedOrder.getMenuName() + " will be delivered in 30 minutes");

        return RestaurantResponse.builder()
                .message("Order is updated successfully")
                .orderDetails(orderResponse)
                .build();

    }
    public RestaurantResponse deleteOrder(RestaurantOrder restaurantOrder) {
        var orderOptional = restaurantOrderRepo.findById(Long.valueOf(restaurantOrder.getOrderId()));
        if(orderOptional.isEmpty()){
            log.info("No order found for order is {} ",restaurantOrder.getOrderId());
            return RestaurantResponse.builder()
                    .message("No order found for orderId : "+restaurantOrder.getOrderId())
                    .build();
        }
        var order = orderOptional.get();
        restaurantOrderRepo.delete(order);

        return RestaurantResponse.builder()
                .message("Order is cancelled successfully")
                .build();
    }

    public String deleteOrder(Long orderId) {

        log.info("Deleting order {}", orderId);
        try {
            restaurantOrderRepo.deleteById(orderId);
        } catch (Exception ex) {
            return "No order found with the orderId " + orderId;
        }
        return "order deleted successfully";
    }

    private RestaurantResponse buildResponse(List<Order> allOrders, String msg) {
        log.info("Total order {}", allOrders.size());
        if (allOrders.size() > 0) {
            var orderDetailsList = getOrderDetailsResponseList(allOrders);
            var restaurantResponse = RestaurantResponse.builder()
                    .message(msg)
                    .orderDetailsResponse(orderDetailsList)
                    .build();
            return restaurantResponse;
        } else {
            return RestaurantResponse.builder()
                    .message(msg)
                    .build();
        }
    }

    private List<OrderDetailsResponse> getOrderDetailsResponseList(List<Order> allOrders) {
        log.info("Received order details for {} orders", allOrders.size());
        return allOrders.stream().map(order -> {
            var orderDetailsResponse = new OrderDetailsResponse();
            BeanUtils.copyProperties(order, orderDetailsResponse);
            return orderDetailsResponse;
        }).collect(Collectors.toList());
    }

}
