package com.example.orderservice.service;

import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.model.OrderLineItem;

public interface OrderService {

    String placeOrder(OrderRequestDto orderRequestDto);

}
