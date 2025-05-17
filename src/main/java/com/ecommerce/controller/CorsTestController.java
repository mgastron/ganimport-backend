package com.ecommerce.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/cors-test")
@CrossOrigin(origins = {"https://www.ganimport.com.ar", "https://ganimport.vercel.app", "http://localhost:3000"}, 
             allowCredentials = "true")
public class CorsTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(CorsTestController.class);
    
    @GetMapping
    public ResponseEntity<?> testCors(HttpServletRequest request) {
        logger.info("Test CORS recibido desde origen: {}", request.getHeader("Origin"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("cors", "ok");
        response.put("origin", request.getHeader("Origin"));
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
} 