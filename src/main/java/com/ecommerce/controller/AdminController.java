package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.service.UserService;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "https://ganimport.vercel.app", "https://www.ganimport.com.ar"})
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        System.out.println("[LOG] POST /api/admin/users - Intento de crear usuario: " + user.getUsername());
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            System.out.println("[LOG] POST /api/admin/users - Acceso denegado para usuario: " + currentUser.getUsername());
            return ResponseEntity.status(403).body("Solo el admin puede crear usuarios");
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsersLog() {
        System.out.println("[LOG] GET /api/admin/users - Acceso detectado");
        return ResponseEntity.status(403).body("GET no permitido en /api/admin/users");
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        try {
            OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado inválido");
        }
    }

    @GetMapping("/ping")
    public String ping() {
        System.out.println("[LOG] /api/admin/ping accedido");
        return "Backend actualizado: 2024-06-10 18:30 - v1";
    }
} 