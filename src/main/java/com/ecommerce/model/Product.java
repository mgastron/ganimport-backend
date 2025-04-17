package com.ecommerce.model;

// Quitamos el import de javax.persistence.Column ya que no lo necesitamos
import java.util.List;

public class Product {
    private String code;
    private String name;
    private Double price;
    private String imageUrl;
    private String category;
    private Integer bulkQuantity;
    private String description;
    private String dimensions;
    private List<String> categories;

    // Constructor vacío
    public Product() {}

    // Getters y setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getBulkQuantity() {
        return bulkQuantity;
    }

    public void setBulkQuantity(Integer bulkQuantity) {
        this.bulkQuantity = bulkQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
} 