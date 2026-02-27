package com.projedata.autoflex.features.production;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.List;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

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
public class GetTotalProductionCapacityIntegrationTest {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    private List<Integer> createdProductIds;
    private List<Integer> createdRawMaterialIds;

    @BeforeEach
    public void initTracking() {
        createdProductIds = new ArrayList<>();
        createdRawMaterialIds = new ArrayList<>();
    }

    @Transactional
    @AfterEach
    public void cleanup() {
        // Delete products first — CascadeType.ALL removes ProductMaterial records,
        // allowing safe deletion of raw materials afterwards
        for (Integer id : createdProductIds) {
            Product p = productRepository.findById(id.longValue());
            if (p != null) productRepository.delete(p);
        }
        for (Integer id : createdRawMaterialIds) {
            RawMaterial rm = rawMaterialRepository.findById(id.longValue());
            if (rm != null) rawMaterialRepository.delete(rm);
        }
    }

    // --- Input validation ---

    @Test
    @DisplayName("Should return 400 Bad Request when strategy parameter exceeds maximum (strategy=2)")
    void shouldReturn400WhenStrategyExceedsMaximum() {
        RestAssured.given()
            .queryParam("strategy", 2)
        .when()
            .get("/api/production")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when strategy parameter is negative (strategy=-1)")
    void shouldReturn400WhenStrategyIsNegative() {
        RestAssured.given()
            .queryParam("strategy", -1)
        .when()
            .get("/api/production")
        .then()
            .statusCode(400);
    }

    // --- strategy=0 (HighestPriceStrategy) ---

    @Test
    @DisplayName("Should calculate correct production capacity for a single product with one material (strategy=0)")
    void shouldCalculateForSingleProductWithOneMaterial_HighestPrice() {
        String ironPayload = """
            {
                "name": "Prod_Iron_T3",
                "initialStock": 60
            }
        """;

        int ironId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(ironPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(ironId);

        int swordId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Sword_T3",
                    "value": 150.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 15}]
                }
            """, ironId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(swordId);

        // 1 ProductMaterial record → no stock duplication
        // Sword: stock=60 / requires=15 per unit → 60/15 = 4 units; totalValue = 150 × 4 = 600.00
        RestAssured.given()
            .queryParam("strategy", 0)
        .when()
            .get("/api/production")
        .then()
            .statusCode(200)
            .body("products.find{it.name == 'Prod_Sword_T3'}.maxProductionCapacity", is(4))
            .body("products.find{it.name == 'Prod_Sword_T3'}.totalValue", is(600.0f));
    }

    @Test
    @DisplayName("Should calculate correct production for two independent products with different materials (strategy=0)")
    void shouldCalculateForTwoProductsWithDifferentMaterials_HighestPrice() {
        String steelPayload = """
            {
                "name": "Prod_Steel_T4",
                "initialStock": 40
            }
        """;

        int steelId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(steelPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(steelId);

        String leatherPayload = """
            {
                "name": "Prod_Leather_T4",
                "initialStock": 30
            }
        """;

        int leatherId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(leatherPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(leatherId);

        int heavySwordId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_HeavySword_T4",
                    "value": 200.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 8}]
                }
            """, steelId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(heavySwordId);

        int bucklerId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Buckler_T4",
                    "value": 100.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 6}]
                }
            """, leatherId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(bucklerId);

        // Each material has exactly 1 ProductMaterial record → no stock duplication
        // HeavySword: 40/8 = 5 units; totalValue = 200 × 5 = 1000.00
        // Buckler:    30/6 = 5 units; totalValue = 100 × 5 =  500.00  (independent materials, no contention)
        RestAssured.given()
            .queryParam("strategy", 0)
        .when()
            .get("/api/production")
        .then()
            .statusCode(200)
            .body("products.find{it.name == 'Prod_HeavySword_T4'}.maxProductionCapacity", is(5))
            .body("products.find{it.name == 'Prod_HeavySword_T4'}.totalValue", is(1000.0f))
            .body("products.find{it.name == 'Prod_Buckler_T4'}.maxProductionCapacity", is(5))
            .body("products.find{it.name == 'Prod_Buckler_T4'}.totalValue", is(500.0f));
    }

    @Test
    @DisplayName("Should apply bottleneck logic when a product requires multiple materials (strategy=0)")
    void shouldApplyBottleneckWhenProductHasMultipleMaterials_HighestPrice() {
        String woodPayload = """
            {
                "name": "Prod_Wood_T5",
                "initialStock": 100
            }
        """;

        int woodId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(woodPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(woodId);

        String nailsPayload = """
            {
                "name": "Prod_Nails_T5",
                "initialStock": 30
            }
        """;

        int nailsId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(nailsPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(nailsId);

        int cabinetId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Cabinet_T5",
                    "value": 250.00,
                    "materials": [
                        {"rawMaterialId": %d, "quantity": 20},
                        {"rawMaterialId": %d, "quantity": 6}
                    ]
                }
            """, woodId, nailsId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(cabinetId);

        // Bottleneck: min(100/20 = 5, 30/6 = 5) = 5 units; totalValue = 250 × 5 = 1250.00
        RestAssured.given()
            .queryParam("strategy", 0)
        .when()
            .get("/api/production")
        .then()
            .statusCode(200)
            .body("products.find{it.name == 'Prod_Cabinet_T5'}.maxProductionCapacity", is(5))
            .body("products.find{it.name == 'Prod_Cabinet_T5'}.totalValue", is(1250.0f));
    }

    @Test
    @DisplayName("Should prioritize higher-value product over lower-value one when both require the same material (strategy=0)")
    void shouldPrioritizeExpensiveProductOverCheaperWhenMaterialIsShared_HighestPrice() {
        // Note: getTotalMaterialsInStock() accumulates stockQuantity once per ProductMaterial record.
        // Since Bronze is linked to 2 products, it appears in 2 records → effective stock = 30+30 = 60.
        String bronzePayload = """
            {
                "name": "Prod_Bronze_T6",
                "initialStock": 30
            }
        """;

        int bronzeId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(bronzePayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(bronzeId);

        int axeId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Axe_T6",
                    "value": 200.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 10}]
                }
            """, bronzeId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(axeId);

        int knifeId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Knife_T6",
                    "value": 100.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 10}]
                }
            """, bronzeId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(knifeId);

        // Each raw material's stock is recorded only once in the map, regardless of how many products use it.
        // Bronze stock = 30 (real)
        // strategy=0 sorts by value desc: Axe (200) → Knife (100)
        // Axe:   30/10 = 3 units → deducts 30 → Bronze stock = 0
        // Knife:  0/10 = 0 → excluded from result
        RestAssured.given()
            .queryParam("strategy", 0)
        .when()
            .get("/api/production")
        .then()
            .statusCode(200)
            .body("products.name", hasItem("Prod_Axe_T6"))
            .body("products.find{it.name == 'Prod_Axe_T6'}.maxProductionCapacity", is(3))
            .body("products.name", not(hasItem("Prod_Knife_T6")));
    }

    // --- strategy=1 (HighestEfficiencyStrategy) ---

    @Test
    @DisplayName("Should prioritize higher-ROI product over higher-value one when both require the same material (strategy=1)")
    void shouldPrioritizeHigherRoiProductOverHigherValueWhenMaterialIsShared_HighestEfficiency() {
        // ROI = product.value / sum(requiredQuantities)
        // Coat:  value=200, quantity=20 → ROI = 200/20 = 10.00
        // Shirt: value=100, quantity=5  → ROI = 100/5  = 20.00  ← higher ROI wins
        // Bronze stock = 30 (real — each raw material counted only once)
        String bronzePayload = """
            {
                "name": "Prod_Bronze_T7",
                "initialStock": 30
            }
        """;

        int bronzeId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(bronzePayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(bronzeId);

        int coatId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Coat_T7",
                    "value": 200.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 20}]
                }
            """, bronzeId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(coatId);

        int shirtId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Shirt_T7",
                    "value": 100.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 5}]
                }
            """, bronzeId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(shirtId);

        // strategy=1 sorts by ROI desc: Shirt (ROI=20) → Coat (ROI=10)
        // Unlike strategy=0, Shirt (cheaper) is prioritized over Coat (more expensive)
        // Shirt: 30/5 = 6 units → deducts all 30 → Bronze stock = 0
        // Coat:   0/20 = 0 → excluded
        RestAssured.given()
            .queryParam("strategy", 1)
        .when()
            .get("/api/production")
        .then()
            .statusCode(200)
            .body("products.name", hasItem("Prod_Shirt_T7"))
            .body("products.find{it.name == 'Prod_Shirt_T7'}.maxProductionCapacity", is(6))
            .body("products.name", not(hasItem("Prod_Coat_T7")));
    }

    @Test
    @DisplayName("Should exclude a product from the result when its material stock is insufficient for even one unit (strategy=1)")
    void shouldExcludeProductWhenStockInsufficientForOneUnit_HighestEfficiency() {
        String platinumPayload = """
            {
                "name": "Prod_Platinum_T8",
                "initialStock": 5
            }
        """;

        int platinumId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(platinumPayload)
        .when()
            .post("/api/raw-materials")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdRawMaterialIds.add(platinumId);

        int relicId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "name": "Prod_Relic_T8",
                    "value": 999.00,
                    "materials": [{"rawMaterialId": %d, "quantity": 10}]
                }
            """, platinumId))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .extract().path("id");
        createdProductIds.add(relicId);

        // 1 ProductMaterial record → no duplication
        // Relic: stock=5 / requires=10 per unit → 5/10 = 0 → excluded from result
        RestAssured.given()
            .queryParam("strategy", 1)
        .when()
            .get("/api/production")
        .then()
            .statusCode(200)
            .body("products.name", not(hasItem("Prod_Relic_T8")));
    }
}
