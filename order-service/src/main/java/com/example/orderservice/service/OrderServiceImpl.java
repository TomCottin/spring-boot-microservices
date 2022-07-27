package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponseDto;
import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItem;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequestDto orderRequestDto) {

        // Instantiate a new order & set the order number attribute
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        // Create a list of oder line item from the dto
        List<OrderLineItem> orderLineItemList = orderRequestDto.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        // Set the order line items attribute for the order
        order.setOrderLineItemList(orderLineItemList);

        // Create a list of skuCode from order line item skuCode attribute
        List<String> skuCodes = order.getOrderLineItemList().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        /*
         Call the inventory API and test for each skuCode if quantity > 0
         Only one request is made thanks to the uriBuilder.queryParam :
         http://localhost:8082/api/v1/inventory?skuCode=iphone_13&skuCode=iphone_13_red
        */
        InventoryResponseDto[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://localhost:8082/api/v1/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponseDto[].class)
                .block();

        assert inventoryResponseArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponseDto::isInStock);

        // Call inventory service, and place order if products are in stock
        if(allProductsInStock) {
            orderRepository.save(order);
            log.info("Order created: {}", order.getId());
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());

        return orderLineItem;

    }

}
