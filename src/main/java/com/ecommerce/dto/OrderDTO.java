package com.ecommerce.dto;

import java.util.List;
import java.time.LocalDateTime;

public class OrderDTO {
    private String orderNumber;
    private List<OrderItemDTO> items;
    private double total;
    private LocalDateTime orderDate;

    // Getters y Setters
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
} 