package com.projedata.autoflex.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "raw_material")
public class RawMaterial {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    public Long id;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "stock_quantity", nullable = false)
    public Integer stockQuantity;

    protected RawMaterial() {

    }

    /**
     * Factory method to create a new RawMaterial instance with the specified name and initial stock quantity. This method validates the input parameters and initializes the raw material with the provided values.
     * @param name the name of the raw material, must not be null or empty
     * @param initialStock the initial stock quantity for the raw material, must be non-negative
     * @return a new RawMaterial instance with the given name and initial stock quantity
     * @throws IllegalArgumentException if the name is null or empty, or if the initial stock quantity is null or negative
     */
    public static RawMaterial create(String name, Integer initialStock) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Raw material name cannot be null or empty");
        }

        if(initialStock == null || initialStock < 0) {
            throw new IllegalArgumentException("Initial stock quantity cannot be null or negative");
        }

        RawMaterial material = new RawMaterial();
        material.name = name;
        material.stockQuantity = initialStock;
        return material;
    }

    /**
     * Deducts the specified quantity from the stock of this raw material. This method validates the input quantity and checks for sufficient stock before performing the deduction.
     * @param quantity the quantity to deduct from the stock, must be greater than zero and less than or equal to the current stock quantity
     * @throws IllegalArgumentException if the quantity is not greater than zero
     * @throws IllegalStateException if the quantity is greater than the current stock quantity
     */
    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to deduct must be greater than zero");
        }

        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock for raw material: " + this.name);
        }

        this.stockQuantity -= quantity;
    }

    /**
     * Sets the stock quantity for this raw material. This method validates the input stock quantity to ensure it is not null or negative before updating the stock quantity.
     * @param stockQuantity the new stock quantity to set, must be non-negative
     * @throws IllegalArgumentException if the stock quantity is null or negative
     */
    public void setStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be null or negative");
        }
        this.stockQuantity = stockQuantity;
    }

    /**
     * Sets the name of this raw material. This method validates the input name to ensure it is not null or empty before updating the name.
     * @param name the new name to set for the raw material, must not be null or empty
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Raw material name cannot be null or empty");
        }
        this.name = name;
    }
}
