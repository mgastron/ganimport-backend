package com.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderItemDTO;
import org.springframework.beans.factory.annotation.Value;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String ADMIN_EMAIL = "matiasgastron@gmail.com";

    public void sendOrderConfirmation(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(ADMIN_EMAIL); // Siempre enviar al email del admin
            message.setSubject("Nuevo Pedido #" + order.getId());
            
            StringBuilder messageText = new StringBuilder();
            messageText.append("Se ha recibido un nuevo pedido:\n\n");
            messageText.append("Detalles del pedido:\n");
            messageText.append("Número de pedido: ").append(order.getId()).append("\n");
            messageText.append("Usuario: ").append(order.getUser().getUsername()).append("\n");
            messageText.append("Estado: ").append(order.getStatus()).append("\n");
            messageText.append("Total: $").append(order.getTotal()).append("\n");
            
            // Agregar detalles de los items del pedido
            messageText.append("\nProductos:\n");
            for (OrderItem item : order.getItems()) {
                messageText.append("- ").append(item.getProductName())
                          .append(" (").append(item.getProductCode()).append(")")
                          .append(" x ").append(item.getQuantity())
                          .append(" ($").append(item.getPricePerUnit()).append(" c/u)\n");
            }
            
            message.setText(messageText.toString());
            
            mailSender.send(message);
            logger.info("Email de notificación enviado a admin para la orden: {}", order.getId());
        } catch (Exception e) {
            logger.error("Error al enviar email: ", e);
            // No lanzar la excepción para que no afecte la creación del pedido
        }
    }
} 