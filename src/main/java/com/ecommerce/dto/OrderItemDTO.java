package com.ecommerce.dto;

public class OrderItemDTO {
    private String productName;
    private String productCode;
    private int quantity;
    private int bulkQuantity;
    private double pricePerUnit;
    private double total;

    // Getters y Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getBulkQuantity() { return bulkQuantity; }
    public void setBulkQuantity(int bulkQuantity) { this.bulkQuantity = bulkQuantity; }
    
    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
} 