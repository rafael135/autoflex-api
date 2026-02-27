package com.projedata.autoflex.features.rawMaterial;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.infrastructure.repositories.ProductMaterialRepository;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

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
public class DeleteRawMaterialIntegrationTest {

    @Inject
    RawMaterialRepository rawMaterialRepository;

    @Inject
    ProductMaterialRepository productMaterialRepository;

    private RawMaterial testRawMaterial;

    @Transactional
    @BeforeEach
    public void createTestRawMaterial() {
        RawMaterial rawMaterial = RawMaterial.create("RawMaterialToDelete", 75);
        this.rawMaterialRepository.persist(rawMaterial);
        this.rawMaterialRepository.flush();
        this.testRawMaterial = rawMaterial;
    }

    @Transactional
    @AfterEach
    public void cleanup() {
        RawMaterial existing = this.rawMaterialRepository.findById(this.testRawMaterial.id);
        if (existing != null) {
            this.productMaterialRepository.delete("rawMaterial.id", existing.id);
            this.rawMaterialRepository.delete(existing);
        }
    }

    @Test
    @DisplayName("Should delete a raw material and return 204 No Content")
    @TestTransaction
    void shouldDeleteRawMaterialAndReturn204() {
        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
        .when()
            .delete("/api/raw-materials/{id}")
        .then()
            .statusCode(204);

        this.rawMaterialRepository.flush();
        RawMaterial deletedRawMaterial = this.rawMaterialRepository.findById(this.testRawMaterial.id);

        assertNull(deletedRawMaterial);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting a non-existent raw material")
    void shouldReturn404WhenDeletingNonExistentRawMaterial() {
        RestAssured.given()
            .pathParam("id", 9999)
        .when()
            .delete("/api/raw-materials/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should return 409 Conflict when deleting a raw material in use by a product")
    void shouldReturn409WhenDeletingRawMaterialInUseByProduct() {
        // Create a product that uses the test raw material
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "ProductUsingRawMaterial",
                    "value": 149.99,
                    "materials": [
                        {
                            "rawMaterialId": %d,
                            "quantity": 5
                        }
                    ]
                }
            """, this.testRawMaterial.id.intValue()))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201);

        // Attempt to delete the raw material while it is referenced by a product
        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
        .when()
            .delete("/api/raw-materials/{id}")
        .then()
            .statusCode(409);
    }
}
