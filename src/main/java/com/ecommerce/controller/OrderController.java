package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.service.EmailService;
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;
import com.ecommerce.model.User;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getOrders() {
        try {
            User currentUser = userService.getCurrentUser();
            List<Order> orders;
            
            if (currentUser.isAdmin()) {
                orders = orderService.getAllOrders();
                logger.info("Admin retrieving all orders");
            } else {
                orders = orderService.getOrdersByUsername(currentUser.getUsername());
                logger.info("User {} retrieving their orders", currentUser.getUsername());
            }
            
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error retrieving orders: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener pedidos: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            User currentUser = userService.getCurrentUser();
            logger.info("Creando orden para usuario: {}", currentUser.getUsername());
            
            Order savedOrder = orderService.createOrder(orderDTO);
            logger.info("Orden creada exitosamente - ID: {}, Total: {}", savedOrder.getId(), savedOrder.getTotal());
            
            emailService.sendOrderConfirmation(savedOrder);
            
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            logger.error("Error al crear orden: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear orden: " + e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody String newStatus) {
        try {
            OrderStatus status = OrderStatus.valueOf(newStatus);
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            logger.error("Error updating order status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el estado: " + e.getMessage());
        }
    }
} 