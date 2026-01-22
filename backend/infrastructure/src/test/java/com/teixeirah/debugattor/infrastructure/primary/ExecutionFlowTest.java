package com.teixeirah.debugattor.infrastructure.primary;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
class ExecutionFlowTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    int port;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void completeAndFailExecutionAndStep() {
        // 1) Start execution
        String executionId =
                given()
                        .when()
                        .post("/api/executions")
                        .then()
                        .statusCode(200)
                        .body("status", equalTo("RUNNING"))
                        .extract().path("id");

        // 2) Register a step
        String stepId =
                given()
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\"Step 1\"}")
                        .when()
                        .post("/api/executions/{id}/steps", executionId)
                        .then()
                        .statusCode(200)
                        .body("status", equalTo("RUNNING"))
                        .extract().path("id");

        // 3) Fail the step
        given()
                .when()
                .post("/api/executions/{execId}/steps/{stepId}/fail", executionId, stepId)
                .then()
                .statusCode(204);

        // 4) Verify step is failed
        given()
                .when()
                .get("/api/executions/{id}", executionId)
                .then()
                .statusCode(200)
                .body(String.format("steps.find { it.id == '%s' }.status", stepId), equalTo("FAILED"));

        // 5) Fail the execution
        given()
                .when()
                .post("/api/executions/{id}/fail", executionId)
                .then()
                .statusCode(204);

        // 6) Verify execution is failed
        given()
                .when()
                .get("/api/executions/{id}", executionId)
                .then()
                .statusCode(200)
                .body("status", equalTo("FAILED"))
                .body("finishedAt", notNullValue());

        // 7) Start another execution
        String execution2Id =
                given()
                        .when()
                        .post("/api/executions")
                        .then()
                        .statusCode(200)
                        .extract().path("id");

        // 8) Complete it
        given()
                .when()
                .post("/api/executions/{id}/complete", execution2Id)
                .then()
                .statusCode(204);

        // 9) Verify it's completed
        given()
                .when()
                .get("/api/executions/{id}", execution2Id)
                .then()
                .statusCode(200)
                .body("status", equalTo("COMPLETED"))
                .body("finishedAt", notNullValue());
    }
}
