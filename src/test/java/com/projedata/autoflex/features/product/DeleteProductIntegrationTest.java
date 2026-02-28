package com.projedata.autoflex.features.product;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DeleteProductIntegrationTest {

    @Inject
    ProductRepository productRepository;

    private Product testProduct;

    @Transactional
    @BeforeEach
    public void createTestProduct() {
        Product product = Product.create("ProductToDelete", BigDecimal.valueOf(99.99));
        this.productRepository.persist(product);
        this.productRepository.flush();
        this.testProduct = product;
    }

    @Transactional
    @AfterEach
    public void cleanup() {
        Product existing = this.productRepository.findById(this.testProduct.id);
        if (existing != null) {
            this.productRepository.delete(existing);
        }
    }

    @Test
    @DisplayName("Should delete a product and return 204 No Content")
    @TestTransaction
    void shouldDeleteProductAndReturn204() {
        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
        .when()
            .delete("/api/products/{id}")
        .then()
            .statusCode(204);

        this.productRepository.flush();
        Product deletedProduct = this.productRepository.findById(this.testProduct.id);

        assertNull(deletedProduct);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting a non-existent product")
    void shouldReturn404WhenDeletingNonExistentProduct() {
        RestAssured.given()
            .pathParam("id", 9999)
        .when()
            .delete("/api/products/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should delete a product with materials and return 204 No Content (cascade)")
    @TestTransaction
    void shouldDeleteProductWithMaterialsAndReturn204() {
        // Create a raw material first
        int rawMaterialId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "MaterialForProductDeletion",
                    "stockQuantity": 50
                }
            """)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Link the raw material to the test product
        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "ProductToDeleteWithMaterial",
                    "value": 99.99,
                    "materials": [
                        {
                            "rawMaterialId": %d,
                            "quantity": 3
                        }
                    ]
                }
            """, rawMaterialId))
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(200);

        // Delete the product — cascade must remove ProductMaterial records
        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
        .when()
            .delete("/api/products/{id}")
        .then()
            .statusCode(204);

        this.productRepository.flush();
        Product deletedProduct = this.productRepository.findById(this.testProduct.id);

        assertNull(deletedProduct);
    }
}
