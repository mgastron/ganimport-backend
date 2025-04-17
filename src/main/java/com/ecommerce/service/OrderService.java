package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.User;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUserUsername(username);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order createOrder(OrderDTO orderDTO) {
        User currentUser = userService.getCurrentUser();
        logger.info("Creando orden para usuario: {} (ID: {})", currentUser.getUsername(), currentUser.getId());
        
        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus(OrderStatus.REALIZADO);
        order.setTotal(orderDTO.getTotal());
        order.setCreatedAt(LocalDateTime.now());
        
        // Convertir items del DTO a entidades
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductName(itemDTO.getProductName());
            item.setProductCode(itemDTO.getProductCode());
            item.setQuantity(itemDTO.getQuantity());
            item.setPricePerUnit(itemDTO.getPricePerUnit());
            items.add(item);
            
            logger.info("Agregando item a la orden - Producto: {}, Cantidad: {}", 
                itemDTO.getProductName(), itemDTO.getQuantity());
        }
        
        order.setItems(items);
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Orden guardada exitosamente - ID: {}, Total: {}", savedOrder.getId(), savedOrder.getTotal());
        
        return savedOrder;
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        if (order.getStatus() == OrderStatus.RECIBIDO) {
            throw new RuntimeException("No se puede modificar una orden ya recibida");
        }
        
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
} 