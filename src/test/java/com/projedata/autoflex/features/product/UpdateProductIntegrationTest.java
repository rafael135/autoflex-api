package com.projedata.autoflex.features.product;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class UpdateProductIntegrationTest {
    
    @Inject
    ProductRepository productRepository;

    private Product testProduct;

    @Transactional
    @BeforeEach
    public void createTestProduct() {
        Product product = Product.create("TestProduct", BigDecimal.valueOf(100.0));
        this.productRepository.persist(product);
        this.productRepository.flush();
        this.testProduct = product;
    }

    @Test
    @DisplayName("Should update a product successfully")
    @TestTransaction
    void shouldUpdateProductSuccessfully() {
        String payload = """
            {
                "name": "UpdatedTestProduct",
                "value": 150.0,
                "materials": []
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(200)
            .body("id", is(this.testProduct.id.intValue()))
            .body("value", is(BigDecimal.valueOf(150.0).floatValue()))
            .body("name", is("UpdatedTestProduct"));

        this.productRepository.flush();
        Product updatedProduct = this.productRepository.findById(this.testProduct.id);

        assertEquals("UpdatedTestProduct", updatedProduct.name);
        assertEquals(BigDecimal.valueOf(150.0).setScale(2), updatedProduct.value);
    }


    @Test
    @DisplayName("Should return 404 Not Found when updating a non-existent product")
    void shouldReturn404WhenUpdatingNonExistentProduct() {
        String payload = """
            {
                "name": "NonExistentProduct",
                "value": 200.0,
                "materials": []
            }
        """;

        RestAssured.given()
            .pathParam("id", 9999)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating a product with invalid data")
    void shouldReturn400WhenUpdatingProductWithInvalidData() {
        String payload = """
            {
                "name": "",
                "value": -50.0,
                "materials": []
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating a product with non-existent material")
    void shouldReturn404WhenUpdatingProductWithNonExistentMaterial() {
        String payload = """
            {
                "name": "TestProductWithNonExistentMaterial",
                "value": 120.0,
                "materials": [
                    {
                        "rawMaterialId": 999999,
                        "quantity": 10
                    }
                ]
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should update a product with valid materials successfully")
    void shouldUpdateProductWithValidMaterialsSuccessfully() {
        // First, create a raw material to be used in the update
        String rawMaterialPayload = """
            {
                "name": "TestRawMaterial",
                "stockQuantity": 100
            }
        """;

        int rawMaterialId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(rawMaterialPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        
        String updatePayload = String.format("""
            {
                "name": "TestProductWithMaterial",
                "value": 130.0,
                "materials": [
                    {
                        "rawMaterialId": %d,
                        "quantity": 5
                    }
                ]
            }
         """, rawMaterialId);

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(updatePayload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(200)
            .body("id", is(this.testProduct.id.intValue()))
            .body("name", is("TestProductWithMaterial"))
            .body("materials[0].rawMaterialId", is(rawMaterialId))
            .body("materials[0].quantity", is(5));
    }


    @Test
    @DisplayName("Should update a product and remove all materials successfully")
    void shouldUpdateProductAndRemoveAllMaterialsSuccessfully() {
        String rawMaterialPayload = """
            {
                "name": "TestRawMaterialForRemoval",
                "stockQuantity": 100
            }
        """;

        int rawMaterialId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(rawMaterialPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        String addMaterialPayload = String.format("""
            {
                "name": "TestProductWithMaterialToRemove",
                "value": 140.0,
                "materials": [
                    {
                        "rawMaterialId": %d,
                        "quantity": 5
                    }
                ]
            }
         """, rawMaterialId);

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(addMaterialPayload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(200);

        // Now, update the product again with an empty materials list to remove all materials
        String updatePayload = """
            {
                "name": "TestProductWithNoMaterials",
                "value": 150.0,
                "materials": []
            }
        """;

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(updatePayload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(200)
            .body("id", is(this.testProduct.id.intValue()))
            .body("value", is(BigDecimal.valueOf(150.0).floatValue()))
            .body("name", is("TestProductWithNoMaterials"))
            .body("materials", hasSize(0));
        
        Product updatedProduct = this.productRepository.findById(this.testProduct.id);

        assertEquals("TestProductWithNoMaterials", updatedProduct.name);
        assertEquals(BigDecimal.valueOf(150.0).setScale(2), updatedProduct.value);
        assertEquals(0, updatedProduct.materials.size());
    }

    @Test
    @DisplayName("Should update a product, adds new materials and remove one existing material successfully")
    void shouldUpdateProductAddNewMaterialsAndRemoveOneExistingMaterialSuccessfully() {
            // First, create two raw materials to be used in the update
            String rawMaterialPayload1 = """
                {
                    "name": "TestRawMaterial1",
                    "stockQuantity": 100
                }
            """;
    
            int rawMaterialId1 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(rawMaterialPayload1)
            .when()
                .post("/api/raw-materials")
            .then()
                .statusCode(201)
                .extract()
                .path("id");
    
            String rawMaterialPayload2 = """
                {
                    "name": "TestRawMaterial2",
                    "stockQuantity": 100
                }
            """;
    
            int rawMaterialId2 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(rawMaterialPayload2)
            .when()
                .post("/api/raw-materials")
            .then()
                .statusCode(201)
                .extract()
                .path("id");
    
            // Add the first material to the product
            String addFirstMaterialPayload = String.format("""
                {
                    "name": "TestProductWithOneMaterial",
                    "value": 140.0,
                    "materials": [
                        {
                            "rawMaterialId": %d,
                            "quantity": 5
                        }
                    ]
                }
            """, rawMaterialId1);
    
            RestAssured.given()
                .pathParam("id", this.testProduct.id.intValue())
                .contentType(ContentType.JSON)
                .body(addFirstMaterialPayload)
            .when()
                .put("/api/products/{id}")
            .then()
                .statusCode(200);
    
            // Now, update the product again by adding a new material and removing the existing one
            String updatePayload = String.format("""
                {
                    "name": "TestProductWithNewAndRemovedMaterials",
                    "value": 150.0,
                    "materials": [
                        {
                            "rawMaterialId": %d,
                            "quantity": 10
                        }
                    ]
                }
            """, rawMaterialId2);
    
            RestAssured.given()
                .pathParam("id", this.testProduct.id.intValue())
                .contentType(ContentType.JSON)
                .body(updatePayload)
            .when()
                .put("/api/products/{id}")
            .then()
                .statusCode(200)
                .body("id", is(this.testProduct.id.intValue()))
                .body("value", is(BigDecimal.valueOf(150.0).floatValue()))
                .body("name", is("TestProductWithNewAndRemovedMaterials"))
                .body("materials", hasSize(1))
                .body("materials[0].rawMaterialId", is(rawMaterialId2))
                .body("materials[0].quantity", is(10));

            Product updatedProduct = this.productRepository.findById(this.testProduct.id);

            assertEquals("TestProductWithNewAndRemovedMaterials", updatedProduct.name);
            assertEquals(BigDecimal.valueOf(150.0).setScale(2), updatedProduct.value);
            assertEquals(1, updatedProduct.materials.size());
            assertEquals(rawMaterialId2, updatedProduct.materials.get(0).rawMaterial.id.intValue());
            assertEquals(10, updatedProduct.materials.get(0).requiredQuantity);
    }

    @Test
    @DisplayName("Should update a product and update the required quantity of an existing material successfully")
    void shouldUpdateProductAndUpdateRequiredQuantityOfExistingMaterialSuccessfully() {
        // First, create a raw material to be used in the update
        String rawMaterialPayload = """
            {
                "name": "TestRawMaterialForQuantityUpdate",
                "stockQuantity": 100
            }
        """;

        int rawMaterialId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(rawMaterialPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Add the material to the product
        String addMaterialPayload = String.format("""
            {
                "name": "TestProductWithMaterialToUpdate",
                "value": 140.0,
                "materials": [
                    {
                        "rawMaterialId": %d,
                        "quantity": 5
                    }
                ]
            }
         """, rawMaterialId);

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(addMaterialPayload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(200);

        // Update the material quantity in the product
        String updatePayload = String.format("""
            {
                "name": "TestProductWithUpdatedMaterialQuantity",
                "value": 150.0,
                "materials": [
                    {
                        "rawMaterialId": %d,
                        "quantity": 10
                    }
                ]
            }
         """, rawMaterialId);

        RestAssured.given()
            .pathParam("id", this.testProduct.id.intValue())
            .contentType(ContentType.JSON)
            .body(updatePayload)
        .when()
            .put("/api/products/{id}")
        .then()
            .statusCode(200)
            .body("id", is(this.testProduct.id.intValue()))
            .body("value", is(BigDecimal.valueOf(150.0).floatValue()))
            .body("name", is("TestProductWithUpdatedMaterialQuantity"))
            .body("materials", hasSize(1))
            .body("materials[0].rawMaterialId", is(rawMaterialId))
            .body("materials[0].quantity", is(10));
        

        Product updatedProduct = this.productRepository.findById(this.testProduct.id);

        assertEquals("TestProductWithUpdatedMaterialQuantity", updatedProduct.name);
        assertEquals(BigDecimal.valueOf(150.0).setScale(2), updatedProduct.value);
        assertEquals(1, updatedProduct.materials.size());
        assertEquals(rawMaterialId, updatedProduct.materials.get(0).rawMaterial.id.intValue());
        assertEquals(10, updatedProduct.materials.get(0).requiredQuantity);
    }
}