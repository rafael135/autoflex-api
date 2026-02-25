package com.projedata.autoflex.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RawMaterialTest {
    

    @Test
    @DisplayName("Should create a RawMaterial with valid name and initial stock")
    void shouldCreateRawMaterialWithValidNameAndInitialStock() {
        // Arrange
        String name = "Steel";
        Integer initialStock = 100;

        // Act
        RawMaterial material = RawMaterial.create(name, initialStock);

        // Assert
        assert material != null;
        assert material.name.equals(name);
        assert material.stockQuantity.equals(initialStock);
    }


    @Test
    @DisplayName("Should deduct stock with valid quantity")
    void shouldDeductStockWithValidQuantity() {
        // Arrange
        RawMaterial material = RawMaterial.create("Aluminum", 50);
        int quantityToDeduct = 20;

        // Act
        material.deductStock(quantityToDeduct);

        // Assert
        assert material.stockQuantity.equals(30);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deducting stock with non-positive quantity")
    void shouldThrowExceptionWhenDeductingStockWithNonPositiveQuantity() {
        RawMaterial material = RawMaterial.create("Plastic", 50);
        int quantityToDeduct = 0;

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> material.deductStock(quantityToDeduct),
            "Expected deductStock() to throw an exception, but it didn't"
        );

        assertEquals("Quantity to deduct must be greater than zero", exception.getMessage());
    }


    @Test
    @DisplayName("Should throw IllegalArgumentException when creating RawMaterial with null name")
    void shouldThrowExceptionWhenCreatingRawMaterialWithNullName() {
        // Arrange
        Integer initialStock = 100;

        // Act
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> RawMaterial.create(null, initialStock),
            "Expected create() to throw an exception, but it didn't"
        );

        // Assert
        assertEquals("Raw material name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating RawMaterial with empty name")
    void shouldThrowExceptionWhenCreatingRawMaterialWithEmptyName() {
        // Arrange
        Integer initialStock = 100;
        
        // Act
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> RawMaterial.create("", initialStock),
            "Expected create() to throw an exception, but it didn't"
        );

        // Assert
        assertEquals("Raw material name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating RawMaterial with negative initial stock")
    void shouldThrowExceptionWhenCreatingRawMaterialWithNegativeInitialStock() {
        // Arrange
        String name = "Copper";
        Integer initialStock = -10;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> RawMaterial.create(name, initialStock),
            "Expected create() to throw an exception, but it didn't"
        );

        // Assert
        assertEquals("Initial stock quantity cannot be null or negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when deducting stock with insufficient quantity")
    void shouldThrowExceptionWhenDeductingStockWithInsufficientQuantity() {
        // Arrange
        RawMaterial material = RawMaterial.create("Glass", 10);
        int quantityToDeduct = 20;
        
        // Act
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> material.deductStock(quantityToDeduct),
            "Expected deductStock() to throw an exception, but it didn't"
        );

        // Assert
        assertEquals("Insufficient stock for raw material: " + material.name, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when setting negative or null stock quantity")
    void shouldThrowExceptionWhenSettingNegativeOrNullStockQuantity() {
        // Arrange
        RawMaterial material = RawMaterial.create("Rubber", 30);
        Integer newStockQuantity = -5;

        // Act
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> material.setStockQuantity(newStockQuantity),
            "Expected setStockQuantity() to throw an exception, but it didn't"
        );
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> material.setStockQuantity(null),
            "Expected setStockQuantity() to throw an exception, but it didn't"
        );

        // Assert
        assertEquals("Stock quantity cannot be null or negative", exception1.getMessage());
        assertEquals("Stock quantity cannot be null or negative", exception2.getMessage());
    }

    
    @Test
    @DisplayName("Should throw IllegalArgumentException when setting null or empty name")
    void shouldThrowExceptionWhenSettingNullOrEmptyName() {
        // Arrange
        RawMaterial material = RawMaterial.create("Wood", 40);
        String newName = "";

        // Act
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> material.setName(newName),
            "Expected setName() to throw an exception, but it didn't"
        );
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> material.setName(null),
            "Expected setName() to throw an exception, but it didn't"
        );

        // Assert
        assertEquals("Raw material name cannot be null or empty", exception1.getMessage());
        assertEquals("Raw material name cannot be null or empty", exception2.getMessage());
    }
}
