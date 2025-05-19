package com.ecommerce.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(HeaderAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String username = request.getHeader("X-Username");
        String uri = request.getRequestURI();
        
        // Log todos los headers para depuración
        String headers = Collections.list(request.getHeaderNames())
            .stream()
            .map(headerName -> headerName + ": " + request.getHeader(headerName))
            .collect(Collectors.joining(", "));
        
        logger.info("Procesando solicitud a '{}' con headers: {}", uri, headers);
        logger.info("X-Username header: '{}'", username);
        
        // Si ya hay una autenticación, no sobrescribirla
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.info("Ya existe una autenticación previa");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Verificar si tenemos un username válido
        if (username != null && !username.trim().isEmpty()) {
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            logger.info("Usuario autenticado por header: {}", username);
        } else {
            logger.warn("No se recibió X-Username válido para: {}", uri);
        }
        
        filterChain.doFilter(request, response);
    }
} 