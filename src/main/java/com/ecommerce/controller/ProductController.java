package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.AngyImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private AngyImportService angyImportService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        logger.info("Recibida solicitud para obtener productos");
        try {
            logger.info("Llamando a AngyImportService.importProducts()");
            List<Product> products = angyImportService.importProducts();
            logger.info("Productos obtenidos: {}", products.size());
            if (products.isEmpty()) {
                logger.warn("La lista de productos está vacía");
            } else {
                logger.info("Primer producto: {}", products.get(0).getName());
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error obteniendo productos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 