package com.projedata.autoflex.domain;

import java.util.List;

import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    public Long id;
    
    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "value", nullable = false)
    public BigDecimal value;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    public List<ProductMaterial> materials = new ArrayList<>();

    protected Product() {}

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.name = name;
    }

    public void setValue(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product value cannot be null or negative");
        }
        this.value = value;
    }

    /**
     * Factory method to create a new Product instance with the specified name. This method validates the input
     * and initializes the product with an empty list of material requirements.
     * @param name the name of the product, must not be null or empty
     * @param value the value of the product, must not be null or negative
     * @return a new Product instance with the given name and no material requirements
     */
    public static Product create(String name, BigDecimal value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product value cannot be null or negative");
        }
        Product product = new Product();
        product.name = name;
        product.value = value;
        return product;
    }

    /**
     * Adds a material requirement to the product. This method creates a new ProductMaterial association
     * between the product and the specified raw material with the given required quantity.
     * @param material the raw material to be added as a requirement
     * @param requiredQuantity the quantity of the raw material required for the product, must be greater than zero
     */
    public void addMaterial(RawMaterial material, int requiredQuantity) {
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException("Required quantity must be greater than zero");
        }

        ProductMaterial association = new ProductMaterial(this, material, requiredQuantity);
        this.materials.add(association);
    }

    /**
     * Removes a material requirement from the product. This method removes the specified ProductMaterial association from the product's list of material requirements.
     * @param productMaterial the ProductMaterial association to be removed from the product's material requirements
     */
    public void removeMaterial(ProductMaterial productMaterial) {
        this.materials.remove(productMaterial);
    }


    /**
     * Calculates the Return on Investment (ROI) for the product based on its value and the total quantity of raw materials required.
     * The ROI is calculated as the product's value divided by the total quantity of raw materials required. If the product does not require any raw materials, the ROI is equal to the product's value.
     * @return the calculated ROI for the product as a BigDecimal
     */
    public BigDecimal calculateRoi() {
        int totalMaterialsRequired = this.materials.stream()
            .mapToInt(m -> m.requiredQuantity)
            .sum();
            
        if (totalMaterialsRequired == 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        
        return this.value.divide(BigDecimal.valueOf(totalMaterialsRequired), 2, RoundingMode.HALF_UP);
    }
}
