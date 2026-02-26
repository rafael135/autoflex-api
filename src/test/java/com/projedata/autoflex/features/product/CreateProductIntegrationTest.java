package com.projedata.autoflex.features.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class CreateProductIntegrationTest {
    

    @Test
    @DisplayName("Should create a product and return 201 Created")
    void shouldCreateProductAndReturn201() {
        // Arrange
        String payload = """
            {
                "name": "Table",
                "value": 299.99,
                "materials": []
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", is("Table"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating a product with invalid data")
    void shouldReturn400WhenCreatingProductWithInvalidData() {
        // Arrange
        String payload = """
            {
                "name": "",
                "value": -10,
                "materials": []
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/products")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 404 Not Found when creating a product with non-existent material")
    void shouldReturn404WhenCreatingProductWithNonExistentMaterial() {
        // Arrange
        String payload = """
            {
                "name": "Chair",
                "value": 199.99,
                "materials": [
                    {
                        "rawMaterialId": 9999,
                        "quantity": 10
                    }
                ]
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/products")
        .then()
            .statusCode(404);
    }


}
