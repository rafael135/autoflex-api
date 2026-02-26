package com.projedata.autoflex.features.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.GreaterThan;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class ListProductsIntegrationTest {
    
    @Test
    @DisplayName("Should list products and return 200 OK")
    void shouldListProductsAndReturn200() {
        RestAssured.given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/products")
        .then()
            .statusCode(200)
            .body("data[0].id", notNullValue())
            .body("data[0].name", is("Table"))
            .body("currentPage", is(1))
            .body("totalPages", is(1));
    }
}
