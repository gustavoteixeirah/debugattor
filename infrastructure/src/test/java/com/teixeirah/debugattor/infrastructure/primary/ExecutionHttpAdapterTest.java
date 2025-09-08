package com.teixeirah.debugattor.infrastructure.primary;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ExecutionHttpAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    int port;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        // Helpful when a test fails
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void startExecution() {
        given()
                .when()
                .post("/api/executions")
                .then()
                .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue());
    }

    @Test
    void fetchExecutions() {
        // ensure at least one exists
        given().when().post("/api/executions").then().statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)));

        given()
                .when()
                .get("/api/executions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    void getExecutionById() {
        // create one and capture ID
        String id =
                given()
                        .when()
                        .post("/api/executions")
                        .then()
                        .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)))
                        .extract()
                        .path("id");

        given()
                .when()
                .get("/api/executions/{id}", id)
                .then()
                .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)))
                .contentType(ContentType.JSON)
                .body("id", equalTo(id));
    }

    @Test
    void registerStep() {
        String id =
                given()
                        .when()
                        .post("/api/executions")
                        .then()
                        .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)))
                        .extract()
                        .path("id");

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"compile\"}")
                .when()
                .post("/api/executions/{id}/steps", id)
                .then()
                .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)))
                .contentType(ContentType.JSON)
                .body("id", equalTo(id));
    }
}
