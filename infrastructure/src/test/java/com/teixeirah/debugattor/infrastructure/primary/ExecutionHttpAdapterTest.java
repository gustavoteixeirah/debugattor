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

import java.util.UUID;

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
                .body("id", notNullValue())
                .body("name", equalTo("compile"))
                .body("status", equalTo("RUNNING"));
    }

    @Test
    void registerStepOnNonExistingExecutionReturns404() {
        String nonExistingId = UUID.randomUUID().toString();

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"compile\"}")
                .when()
                .post("/api/executions/{id}/steps", nonExistingId)
                .then()
                .statusCode(404);
    }

    @Test
    void logArtifact() {
        // 1) Create execution
        String executionId =
                given()
                        .when()
                        .post("/api/executions")
                        .then()
                        .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)))
                        .extract().path("id");

        // 2) Register a step
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Converted to BW\"}")
                .when()
                .post("/api/executions/{id}/steps", executionId)
                .then()
                .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)));

        // 3) Fetch execution to get the stepId
        String stepId =
                given()
                        .when()
                        .get("/api/executions/{id}", executionId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("steps[0].id");

        // 4) Log an artifact for that step
        given()
                .contentType(ContentType.JSON)
                .body("{\"type\":\"LOG\",\"content\":\"Converted to BW\"}")
                .when()
                .post("/api/executions/{execId}/steps/{stepId}/artifacts", executionId, stepId)
                .then()
                .statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("type", equalTo("LOG"))
                .body("content", equalTo("Converted to BW"))
                .body("loggedAt", notNullValue());

        // 5) Assert via GET /api/executions (fetch all) that the artifact is persisted under the correct execution/step
        given()
                .when()
                .get("/api/executions")
                .then()
                .statusCode(200)
                // execution exists
                .body(String.format("find { it.id == '%s' }", executionId), notNullValue())
                // step exists under that execution
                .body(String.format("find { it.id == '%s' }.steps.find { it.id == '%s' }", executionId, stepId), notNullValue())
                // artifacts not empty
                .body(String.format("find { it.id == '%s' }.steps.find { it.id == '%s' }.artifacts", executionId, stepId), not(empty()))
                // an artifact with the expected type/content exists
                .body(String.format(
                                "find { it.id == '%s' }.steps.find { it.id == '%s' }.artifacts.find { it.type == 'LOG' && it.content == 'Converted to BW' }",
                                executionId, stepId),
                        notNullValue());
    }

    @Test
    void logArtifactOnNonExistingStepReturns404() {
        String nonExistingId = UUID.randomUUID().toString();

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "type":"LOG",
                            "content":"Converted to BW"
                        }
                        """)
                .when()
                .post("/api/executions/{executionId}/steps/{stepId}/artifacts", nonExistingId, nonExistingId)
                .then()
                .statusCode(404);
    }

}
