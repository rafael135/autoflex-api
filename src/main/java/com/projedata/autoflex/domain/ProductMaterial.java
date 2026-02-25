package com.projedata.autoflex.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "product_material",
    uniqueConstraints = @UniqueConstraint(columnNames = { "product_id", "raw_material_id" })
)
public class ProductMaterial {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raw_material_id", nullable = false)
    public RawMaterial rawMaterial;

    @Column(name = "required_quantity", nullable = false)
    public int requiredQuantity;

    protected ProductMaterial() {}

    ProductMaterial(Product product, RawMaterial rawMaterial, Integer requiredQuantity) {
        this.product = product;
        this.rawMaterial = rawMaterial;
        this.requiredQuantity = requiredQuantity;
    }

    /**
     * Sets the required quantity of raw material for this product material association. This method validates the input quantity to ensure it is greater than zero before updating the required quantity.
     * @param requiredQuantity the quantity of the raw material required for the product, must be greater than zero
     * @throws IllegalArgumentException if the required quantity is not greater than zero
     */
    public void setRequiredQuantity(int requiredQuantity) {
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException("Required quantity must be greater than zero");
        }
        this.requiredQuantity = requiredQuantity;
    }
}
