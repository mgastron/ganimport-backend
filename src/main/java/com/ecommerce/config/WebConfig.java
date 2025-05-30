package com.ecommerce.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public CorsFilter corsFilter() {
        logger.info("Inicializando CorsFilter con configuración mejorada");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir orígenes específicos - no usar "*" cuando se usan credenciales
        config.addAllowedOrigin("https://www.ganimport.com.ar");
        config.addAllowedOrigin("https://ganimport.vercel.app");
        config.addAllowedOrigin("http://localhost:3000");
        logger.info("Orígenes CORS configurados: www.ganimport.com.ar, ganimport.vercel.app, localhost:3000");
        
        config.setAllowCredentials(true);
        
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("X-Auth-Token");
        config.addAllowedHeader("X-Username");
        
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        config.addExposedHeader("X-Auth-Token");
        config.addExposedHeader("X-Username");
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        logger.info("Configuración CORS completada con orígenes específicos y credenciales habilitadas");
        
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor());
    }
} 