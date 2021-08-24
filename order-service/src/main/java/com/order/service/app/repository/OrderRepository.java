package com.order.service.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.service.app.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}