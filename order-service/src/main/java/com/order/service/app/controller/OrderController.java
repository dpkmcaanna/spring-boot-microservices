package com.order.service.app.controller;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.order.service.app.dto.OrderDto;
import com.order.service.app.entity.Order;
import com.order.service.app.repository.OrderRepository;
import com.order.service.app.rest.client.InventoryClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final ExecutorService traceableExecutorService;
    
    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderDto orderDto) {
    	circuitBreakerFactory.configureExecutorService(traceableExecutorService);
        Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("inventory");
        log.info("Received input: {}", orderDto);
        java.util.function.Supplier<Boolean> booleanSupplier = () -> orderDto.getOrderLineItemsList().stream()
                .allMatch(lineItem -> {
                    log.info("Making Call to Inventory Service for SkuCode {}", lineItem.getSkuCode());
                    return inventoryClient.checkStock(lineItem.getSkuCode());
                });
        boolean productsInStock = circuitBreaker.run(booleanSupplier, throwable -> handleErrorCase());

        if (productsInStock) {
        	Order order = new Order();
            order.setOrderLineItems(orderDto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());
            Order savedOrder = orderRepository.saveAndFlush(order);
            
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedOrder.getId())
                    .toUri();
            
            log.info("Sending Order Details with Order Id {} to Notification Service", order.getId());
            return ResponseEntity.created(uri).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body("Order Failed - One of the Product in your Order is out of stock");
        }
    }
    
    private Boolean handleErrorCase() {
        return false;
    }
}