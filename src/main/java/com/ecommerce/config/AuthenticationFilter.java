package com.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        logger.info("Procesando solicitud: {} {}", method, requestURI);
        
        // Verifica si estamos en la ruta de login
        boolean isLoginRequest = requestURI.contains("/api/auth/login") && "POST".equals(method);
        
        if (isLoginRequest) {
            logger.info("Detectada solicitud de login: {}", requestURI);
            processLoginRequest(request, response, filterChain);
        } else {
            // Para cualquier otra ruta, simplemente continúa la cadena de filtros
            logger.info("Solicitud no relacionada con login, continuando: {}", requestURI);
            filterChain.doFilter(request, response);
        }
    }
    
    private void processLoginRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            // Crear un wrapper para la solicitud que permita leer el cuerpo varias veces
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            
            // Lee el cuerpo de la solicitud
            String body = cachedRequest.getReader().lines().collect(Collectors.joining());
            
            // Log del cuerpo para depuración
            logger.info("Cuerpo de solicitud login: {}", body);
            System.out.println("Cuerpo de solicitud recibida: " + body);
            
            if (body != null && !body.trim().isEmpty()) {
                try {
                    // Parsea el cuerpo JSON
                    Map<String, Object> requestBody = objectMapper.readValue(body, Map.class);
                    System.out.println("Cuerpo parseado: " + requestBody);
                    
                    if (requestBody.containsKey("username")) {
                        String username = (String) requestBody.get("username");
                        logger.info("Autenticando usuario: {}", username);
                        
                        if (username != null && !username.trim().isEmpty()) {
                            // Crea token de autenticación
                            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                            var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            
                            logger.info("Usuario autenticado exitosamente: {}", username);
                            System.out.println("Usuario autenticado: " + username);
                        } else {
                            logger.warn("Username vacío o nulo");
                        }
                    } else {
                        logger.warn("JSON no contiene campo 'username'");
                    }
                } catch (Exception e) {
                    logger.error("Error al parsear JSON: {}", e.getMessage(), e);
                    System.out.println("Error al parsear JSON: " + e.getMessage());
                }
            } else {
                logger.warn("Cuerpo de solicitud vacío");
            }
            
            // Continúa con la cadena de filtros pasando la solicitud en caché
            filterChain.doFilter(cachedRequest, response);
        } catch (Exception e) {
            logger.error("Error general en el procesamiento de login: {}", e.getMessage(), e);
            System.out.println("Error en filtro: " + e.getMessage());
            // En caso de error, intenta continuar con el proceso original
            filterChain.doFilter(request, response);
        }
    }
    
    // Clase interna para permitir leer el cuerpo de la solicitud múltiples veces
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;
        
        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            
            // Lee el cuerpo en un arreglo de bytes (esto permite leerlo múltiples veces)
            String bodyString = "";
            try (BufferedReader bufferedReader = request.getReader()) {
                if (bufferedReader != null) {
                    bodyString = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            } catch (Exception e) {
                System.out.println("Error al leer body original: " + e.getMessage());
            }
            
            this.cachedBody = bodyString.getBytes();
        }
        
        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
            
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }
                
                @Override
                public boolean isReady() {
                    return true;
                }
                
                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }
        
        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }
    }
} 