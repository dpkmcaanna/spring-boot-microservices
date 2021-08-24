package com.order.service.app.controller;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.order.service.app.dto.OrderDto;
import com.order.service.app.entity.Order;
import com.order.service.app.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderDto orderDto) {
    	Order order = new Order();
        order.setOrderLineItems(orderDto.getOrderLineItemsList());
        order.setOrderNumber(UUID.randomUUID().toString());
        Order savedOrder = orderRepository.saveAndFlush(order);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrder.getId())
                .toUri();
        
        return ResponseEntity.created(uri).build();
    }
}