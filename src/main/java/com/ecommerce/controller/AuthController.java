package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login attempt received from origin: {}", request.getHeader("Origin"));
        logger.info("Request headers: {}", Collections.list(request.getHeaderNames())
            .stream()
            .collect(Collectors.toMap(
                header -> header,
                request::getHeader
            )));
        
        try {
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            
            // Generar un token simple basado en UUID (en producción real usaríamos JWT)
            String token = UUID.randomUUID().toString();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("isAdmin", user.isAdmin());
            responseData.put("username", user.getUsername());
            responseData.put("message", "Inicio de sesión exitoso");
            
            // Establecer headers de respuesta
            response.setHeader("X-Auth-Token", token);
            response.setHeader("X-Username", user.getUsername());
            response.setHeader("Access-Control-Expose-Headers", "X-Auth-Token, X-Username");
            
            logger.info("Login successful for user: {}", user.getUsername());
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            logger.error("Authentication failed: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Endpoint para verificar estado de autenticación
    @GetMapping("/verify")
    public ResponseEntity<?> verifyAuthentication(HttpServletRequest request) {
        String username = request.getHeader("X-Username");
        logger.info("Verificando autenticación para: {}", username);
        
        if (username != null && !username.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username", username
            ));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("authenticated", false));
    }
}