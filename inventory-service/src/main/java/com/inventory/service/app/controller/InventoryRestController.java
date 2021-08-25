package com.inventory.service.app.controller;

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

import com.inventory.service.app.entity.Inventory;
import com.inventory.service.app.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryRestController {

    private final InventoryRepository inventoryRepository;

    @GetMapping("/skuCode/{skuCode}")
    Boolean isInStock(@PathVariable String skuCode) {
        log.info("Checking stock for product with skucode - " + skuCode);
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("Cannot Find Product by sku code " + skuCode));
        return inventory.getStock() > 0;
    }
    
    @GetMapping("/id/{id}")
    ResponseEntity<Inventory> findById(@PathVariable Long id) {
        log.info("Checking stock for product with Id - " + id);
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot Find Product by id " + id));
        return ResponseEntity.ok(inventory);
    }
    
    @RequestMapping
    ResponseEntity<List<Inventory>> findAll() {
    	 log.info("Checking entire enventory stock ");
    	 List<Inventory> listsInventories = inventoryRepository.findAll();
    	 return ResponseEntity.ok(listsInventories);
    }
    
    @PostMapping
    public ResponseEntity<String> saveInventory(@RequestBody Inventory inventory) {
    	Inventory savedInventory = inventoryRepository.saveAndFlush(inventory);
    	 URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                 .path("/{id}")
                 .buildAndExpand(savedInventory.getId())
                 .toUri();
         return ResponseEntity.created(uri).build();
    }
}