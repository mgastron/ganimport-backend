package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HttpServletRequest request;

    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public User authenticate(String username, String password) {
        logger.info("Intentando autenticar usuario: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Aquí deberías tener tu lógica de autenticación real
        // Por ahora, simplemente guardamos el username y devolvemos el usuario
        setCurrentUser(username);
        logger.info("Usuario autenticado exitosamente: {}", username);
        
        return user; // Devolvemos el usuario en lugar de boolean
    }

    public void setCurrentUser(String username) {
        logger.info("Estableciendo usuario actual: {}", username);
        currentUsername.set(username);
    }

    public User getCurrentUser() {
        String username = request.getHeader("X-Username");
        logger.info("Headers recibidos: {}", Collections.list(request.getHeaderNames()));
        logger.info("Intentando obtener usuario desde header X-Username: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            logger.error("No se encontró X-Username en los headers");
            throw new RuntimeException("No se encontró usuario en la sesión");
        }
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.error("No se encontró usuario en la base de datos para username: {}", username);
                return new RuntimeException("Usuario no encontrado: " + username);
            });
    }

    public User createUser(User user) {
        logger.info("Creando nuevo usuario: {}", user.getUsername());
        
        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe: " + user.getUsername());
        }
        
        User savedUser = userRepository.save(user);
        logger.info("Usuario creado exitosamente: {}", savedUser.getUsername());
        
        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
} 