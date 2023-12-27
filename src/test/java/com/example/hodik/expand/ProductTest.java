package com.example.hodik.expand;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

public class ProductTest {


    @LocalServerPort
    private int port;

    private final String expectedRecords = TestUtils.readResource("expected.products.records.json");
    private final String expectedRecordsFromDB = TestUtils.readResource("expected.records.from.db.json");
    private final String product = TestUtils.readResource("products.json");
    private final String productNew = TestUtils.readResource("products.new.json");

    private final String expectedUserBody = TestUtils.readResource("expected.user.body.json");
    private String accessToken;


    @BeforeEach
    public void setupUserAuthentication() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(expectedUserBody)
                .when()
                .post("/users/add");

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(expectedUserBody)
                .when()
                .post("/users/authenticate");

        accessToken = response.jsonPath().getString("access_token");

    }

    @Test
    public void testCreateProduct() {
        String actualResponseBody = given()
                .port(port)
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .body(product)
                .when()
                .post("/products/add")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract().asString();

        JsonElement expectedJson = JsonParser.parseString(expectedRecords);
        JsonElement actualJson = JsonParser.parseString(actualResponseBody);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testFindAllProducts() {

        String actualResponseBody = given()
                .port(port)
                .auth().oauth2(accessToken)
                .when()
                .get("/products/all")
                .then()
                .statusCode(200)
                .extract().asString();

        JsonElement expectedJson = JsonParser.parseString(expectedRecordsFromDB);
        JsonElement actualJson = JsonParser.parseString(actualResponseBody);

        assertEquals(expectedJson, actualJson);
    }


    @Test
    public void testAddProductsNewAndCreateTable() {

        String actualResponseBody = given()
                .port(port)
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .body(productNew)
                .when()
                .post("/products/add")
                .then()
                .statusCode(200)
                .extract().asString();
        JsonElement expectedJson = JsonParser.parseString(expectedRecords);
        JsonElement actualJson = JsonParser.parseString(actualResponseBody);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testAddProductWithoutAuthenticationShouldBeForbidden() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products/add")
                .then()
                .statusCode(403);
    }

    @Test
    public void testFindProductWithoutAuthenticationShouldBeForbidden() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products/all")
                .then()
                .statusCode(403);
    }
}