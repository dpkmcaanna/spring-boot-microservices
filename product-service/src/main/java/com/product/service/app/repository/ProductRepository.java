package com.product.service.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product.service.app.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}