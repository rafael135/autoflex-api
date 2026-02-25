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

    @Test
    @DisplayName("Should update product name and value with valid inputs")
    void shouldUpdateProductNameAndValueWithValidInputs() {
        // Arrange
        Product product = Product.create("Sofa", new BigDecimal("499.99"));
        String newName = "Luxury Sofa";
        BigDecimal newValue = new BigDecimal("799.99");

        // Act
        product.setName(newName);
        product.setValue(newValue);

        // Assert
        assert product.name.equals(newName);
        assert product.value.equals(newValue);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when setting null or empty name")
    void shouldThrowExceptionWhenSettingNullOrEmptyName() {
        // Arrange
        Product product = Product.create("Desk", new BigDecimal("299.99"));
        String newName = "";

        // Act
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> product.setName(newName),
            "Expected setName() to throw an exception for empty name, but it didn't"
        );

        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> product.setName(null),
            "Expected setName() to throw an exception for null name, but it didn't"
        );

        // Assert
        assertEquals("Product name cannot be null or empty", exception1.getMessage());
        assertEquals("Product name cannot be null or empty", exception2.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when setting null or negative value")
    void shouldThrowExceptionWhenSettingNullOrNegativeValue() {
        // Arrange
        Product product = Product.create("Bookshelf", new BigDecimal("149.99"));
        BigDecimal newValue = new BigDecimal("-50.00");

        // Act
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> product.setValue(newValue),
            "Expected setValue() to throw an exception for negative value, but it didn't"
        );

        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> product.setValue(null),
            "Expected setValue() to throw an exception for null value, but it didn't"
        );

        // Assert
        assertEquals("Product value cannot be null or negative", exception1.getMessage());
        assertEquals("Product value cannot be null or negative", exception2.getMessage());
    }

    @Test
    @DisplayName("Should remove material requirements from product")
    void shouldRemoveMaterialRequirementsFromProduct() {
        // Arrange
        Product product = Product.create("Table", new BigDecimal("299.99"));
        RawMaterial wood = RawMaterial.create("Wood", 100);
        RawMaterial metal = RawMaterial.create("Metal", 50);
        product.addMaterial(wood, 10);
        product.addMaterial(metal, 5);

        // Act
        ProductMaterial pmToRemove = product.materials.get(0); // Assuming we want to remove the first material requirement
        product.removeMaterial(pmToRemove);

        // Assert
        assert !product.materials.contains(pmToRemove);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding material with non-positive required quantity")
    void shouldThrowExceptionWhenAddingMaterialWithNonPositiveRequiredQuantity() {
        // Arrange
        Product product = Product.create("Lamp", new BigDecimal("89.99"));
        RawMaterial glass = RawMaterial.create("Glass", 200);
        int requiredQuantity = 0;

        // Act
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> product.addMaterial(glass, requiredQuantity),
            "Expected addMaterial() to throw an exception for non-positive required quantity, but it didn't"
        );

        // Assert
        assertEquals("Required quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should add material requirement to product with valid inputs")
    void shouldAddMaterialRequirementToProductWithValidInputs() {
        // Arrange
        Product product = Product.create("Bed", new BigDecimal("399.99"));
        RawMaterial fabric = RawMaterial.create("Fabric", 150);
        int requiredQuantity = 20;

        // Act
        product.addMaterial(fabric, requiredQuantity);

        // Assert
        assert product.materials.size() == 1;
        ProductMaterial pm = product.materials.get(0);
        assert pm.rawMaterial.equals(fabric);
        assert pm.requiredQuantity == requiredQuantity;
    }
}
