package com.projedata.autoflex.features.rawMaterial;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class CreateRawMaterialIntegrationTest {

    @Test
    @DisplayName("Should create a raw material and return 201 Created")
    void shouldCreateRawMaterialAndReturn201() {
        String payload = """
            {
                "name": "Steel Bar",
                "stockQuantity": 50
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", is("Steel Bar"))
            .body("stockQuantity", is(50));
    }

    @Test
    @DisplayName("Should create a raw material with zero stock quantity and return 201 Created")
    void shouldCreateRawMaterialWithZeroStockQuantityAndReturn201() {
        String payload = """
            {
                "name": "Empty Stock Material",
                "stockQuantity": 0
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", is("Empty Stock Material"))
            .body("stockQuantity", is(0));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating a raw material with empty name")
    void shouldReturn400WhenCreatingWithEmptyName() {
        String payload = """
            {
                "name": "",
                "stockQuantity": 10
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating a raw material with null name")
    void shouldReturn400WhenCreatingWithNullName() {
        String payload = """
            {
                "stockQuantity": 10
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating a raw material with negative stock quantity")
    void shouldReturn400WhenCreatingWithNegativeStockQuantity() {
        String payload = """
            {
                "name": "Invalid Material",
                "stockQuantity": -1
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating a raw material with null stock quantity")
    void shouldReturn400WhenCreatingWithNullStockQuantity() {
        String payload = """
            {
                "name": "No Stock Material"
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(400);
    }
}
