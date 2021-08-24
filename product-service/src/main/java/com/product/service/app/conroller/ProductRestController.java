package com.product.service.app.conroller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.product.service.app.entity.Product;
import com.product.service.app.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductRestController {
 
    private final ProductRepository productRepository;
 
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
 
    @GetMapping("/{id}")
    public Product getAllProducts(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot Find Product By ID: " + id));
    }
 
    @PostMapping
    public ResponseEntity<String> saveProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.saveAndFlush(product);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProduct.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
