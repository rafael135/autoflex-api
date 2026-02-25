package com.projedata.autoflex.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductMaterialTest {
    
    @Test
    @DisplayName("Should create a ProductMaterial with valid product, raw material and required quantity")
    void shouldCreateProductMaterialWithValidProductRawMaterialAndRequiredQuantity() {
        // Arrange
        Product product = Product.create("Table", new java.math.BigDecimal("299.99"));
        RawMaterial rawMaterial = RawMaterial.create("Wood", 100);
        int requiredQuantity = 5;

        // Act
        ProductMaterial productMaterial = new ProductMaterial(product, rawMaterial, requiredQuantity);

        // Assert
        assert productMaterial != null;
        assert productMaterial.product.equals(product);
        assert productMaterial.rawMaterial.equals(rawMaterial);
        assert productMaterial.requiredQuantity == requiredQuantity;
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when setting required quantity to zero or negative")
    void shouldThrowExceptionWhenSettingRequiredQuantityToZeroOrNegative() {
        Product product = Product.create("Chair", new java.math.BigDecimal("199.99"));
        RawMaterial rawMaterial = RawMaterial.create("Metal", 50);
        ProductMaterial productMaterial = new ProductMaterial(product, rawMaterial, 10);

        IllegalArgumentException exception1 = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> productMaterial.setRequiredQuantity(0),
            "Expected setRequiredQuantity() to throw an exception for zero quantity, but it didn't"
        );

        IllegalArgumentException exception2 = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> productMaterial.setRequiredQuantity(-5),
            "Expected setRequiredQuantity() to throw an exception for negative quantity, but it didn't"
        );

        assert exception1.getMessage().equals("Required quantity must be greater than zero");
        assert exception2.getMessage().equals("Required quantity must be greater than zero");
    }
}
