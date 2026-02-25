package com.projedata.autoflex.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductTest {
    

    @Test
    @DisplayName("Should create a Product with valid name and value")
    void shouldCreateProductWithValidNameAndValue() {
        // Arrange
        String name = "Chair";
        BigDecimal value = new BigDecimal("199.99");

        // Act
        Product product = Product.create(name, value);

        // Assert
        assert product != null;
        assert product.name.equals(name);
        assert product.value.equals(value);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating a Product with null or empty name")
    void shouldThrowExceptionWhenCreatingProductWithNullOrEmptyName() {
        BigDecimal value = new BigDecimal("199.99");

        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> Product.create(null, value),
            "Expected create() to throw an exception for null name, but it didn't"
        );

        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> Product.create("   ", value),
            "Expected create() to throw an exception for empty name, but it didn't"
        );

        assertEquals("Product name cannot be null or empty", exception1.getMessage());
        assertEquals("Product name cannot be null or empty", exception2.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating a Product with null or negative value")
    void shouldThrowExceptionWhenCreatingProductWithNullOrNegativeValue() {
        String name = "Table";

        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> Product.create(name, null),
            "Expected create() to throw an exception for null value, but it didn't"
        );

        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> Product.create(name, new BigDecimal("-10.00")),
            "Expected create() to throw an exception for negative value, but it didn't"
        );

        assertEquals("Product value cannot be null or negative", exception1.getMessage());
        assertEquals("Product value cannot be null or negative", exception2.getMessage());
    }
}
