package com.projedata.autoflex.features.rawMaterial;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.projedata.autoflex.domain.RawMaterial;
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
public class UpdateRawMaterialIntegrationTest {

    @Inject
    RawMaterialRepository rawMaterialRepository;

    private RawMaterial testRawMaterial;

    @Transactional
    @BeforeEach
    public void createTestRawMaterial() {
        RawMaterial rawMaterial = RawMaterial.create("TestRawMaterialForUpdate", 100);
        this.rawMaterialRepository.persist(rawMaterial);
        this.rawMaterialRepository.flush();
        this.testRawMaterial = rawMaterial;
    }

    @Transactional
    @AfterEach
    public void cleanup() {
        RawMaterial existing = this.rawMaterialRepository.findById(this.testRawMaterial.id);
        if (existing != null) {
            this.rawMaterialRepository.delete(existing);
        }
    }

    @Test
    @DisplayName("Should update a raw material successfully")
    @TestTransaction
    void shouldUpdateRawMaterialSuccessfully() {
        String payload = """
            {
                "name": "UpdatedRawMaterial",
                "stockQuantity": 200
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/raw-materials/{id}")
        .then()
            .statusCode(200)
            .body("id", is(this.testRawMaterial.id.intValue()))
            .body("name", is("UpdatedRawMaterial"))
            .body("stockQuantity", is(200));

        this.rawMaterialRepository.flush();
        RawMaterial updatedRawMaterial = this.rawMaterialRepository.findById(this.testRawMaterial.id);

        assertEquals("UpdatedRawMaterial", updatedRawMaterial.name);
        assertEquals(200, updatedRawMaterial.stockQuantity);
    }

    @Test
    @DisplayName("Should update a raw material stock to zero successfully")
    @TestTransaction
    void shouldUpdateRawMaterialStockToZeroSuccessfully() {
        String payload = """
            {
                "name": "ZeroStockMaterial",
                "stockQuantity": 0
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/raw-materials/{id}")
        .then()
            .statusCode(200)
            .body("id", is(this.testRawMaterial.id.intValue()))
            .body("name", is("ZeroStockMaterial"))
            .body("stockQuantity", is(0));

        this.rawMaterialRepository.flush();
        RawMaterial updatedRawMaterial = this.rawMaterialRepository.findById(this.testRawMaterial.id);

        assertEquals("ZeroStockMaterial", updatedRawMaterial.name);
        assertEquals(0, updatedRawMaterial.stockQuantity);
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating a non-existent raw material")
    void shouldReturn404WhenUpdatingNonExistentRawMaterial() {
        String payload = """
            {
                "name": "NonExistentMaterial",
                "stockQuantity": 50
            }
        """;

        RestAssured.given()
            .pathParam("id", 9999)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/raw-materials/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating a raw material with empty name")
    void shouldReturn400WhenUpdatingWithEmptyName() {
        String payload = """
            {
                "name": "",
                "stockQuantity": 50
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/raw-materials/{id}")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating a raw material with null name")
    void shouldReturn400WhenUpdatingWithNullName() {
        String payload = """
            {
                "stockQuantity": 50
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/raw-materials/{id}")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating a raw material with negative stock quantity")
    void shouldReturn400WhenUpdatingWithNegativeStockQuantity() {
        String payload = """
            {
                "name": "InvalidQuantityMaterial",
                "stockQuantity": -1
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/raw-materials/{id}")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating a raw material with null stock quantity")
    void shouldReturn400WhenUpdatingWithNullStockQuantity() {
        String payload = """
            {
                "name": "NullStockMaterial"
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testRawMaterial.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/raw-materials/{id}")
        .then()
            .statusCode(400);
    }
}
