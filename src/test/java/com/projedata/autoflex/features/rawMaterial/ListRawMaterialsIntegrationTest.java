package com.projedata.autoflex.features.rawMaterial;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.containsStringIgnoringCase;

@QuarkusTest
public class ListRawMaterialsIntegrationTest {

    @Test
    @DisplayName("Should list raw materials and return 200 OK")
    void shouldListRawMaterialsAndReturn200() {
        // Ensure at least one raw material exists
        String payload = """
            {
                "name": "Listing Test Material",
                "initialStock": 25
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201);

        RestAssured.given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/raw-materials")
        .then()
            .statusCode(200)
            .body("data[0].id", notNullValue())
            .body("data[0].name", notNullValue())
            .body("data[0].stockQuantity", notNullValue())
            .body("currentPage", greaterThanOrEqualTo(1))
            .body("totalPages", greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Should return paginated response with default pagination settings")
    void shouldReturnPaginatedResponseWithDefaultPagination() {
        RestAssured.given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/raw-materials")
        .then()
            .statusCode(200)
            .body("currentPage", greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Should return paginated response with custom page and itemsPerPage")
    void shouldReturnPaginatedResponseWithCustomPagination() {
        // Create two raw materials to ensure pagination data is meaningful
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Pagination Material A",
                    "initialStock": 10
                }
            """)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201);

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Pagination Material B",
                    "initialStock": 20
                }
            """)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201);

        RestAssured.given()
            .contentType(ContentType.JSON)
            .queryParam("page", 1)
            .queryParam("itemsPerPage", 1)
        .when()
            .get("/api/raw-materials")
        .then()
            .statusCode(200)
            .body("currentPage", greaterThanOrEqualTo(1))
            .body("data.size()", lessThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when page is negative")
    void shouldReturn400WhenPageIsNegative() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .queryParam("page", -1)
        .when()
            .get("/api/raw-materials")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when itemsPerPage exceeds maximum")
    void shouldReturn400WhenItemsPerPageExceedsMaximum() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .queryParam("itemsPerPage", 101)
        .when()
            .get("/api/raw-materials")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when itemsPerPage is zero")
    void shouldReturn400WhenItemsPerPageIsZero() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .queryParam("itemsPerPage", 0)
        .when()
            .get("/api/raw-materials")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return filtered results when name query parameter is provided")
    void shouldReturnFilteredResultsWhenNameQueryParameterIsProvided() {
        String uniqueName = "Unique Material Name " + System.currentTimeMillis();

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "%s",
                    "initialStock": 15
                }
            """.formatted(uniqueName))
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201);

        RestAssured.given()
            .contentType(ContentType.JSON)
            .queryParam("name", uniqueName)
        .when()
            .get("/api/raw-materials")
        .then()
            .statusCode(200)
            .body("data.size()", greaterThanOrEqualTo(1))
            .body("data[0].name", notNullValue())
            .body("data[0].name", containsStringIgnoringCase(uniqueName));
    }
}
