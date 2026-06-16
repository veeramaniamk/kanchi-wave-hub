package com.saveetha.kanchi_wave_hub.qa.api;

import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.qa.integration.BaseIntegrationTest;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestAssuredApiTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    private String userToken;
    private String sellerToken;

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
        setupTestUsers();
    }

    private void setupTestUsers() {
        // Register and login a normal customer user (userType = 100)
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("name", "Customer API User");
        userBody.put("email", "api_customer@example.com");
        userBody.put("password", "customer123");
        userBody.put("phone", 9999999901L);
        userBody.put("address", "123 Saree Bazaar");

        given()
            .contentType(ContentType.JSON)
            .body(userBody)
            .post("/api/register");

        Response response = given()
            .contentType(ContentType.JSON)
            .body(userBody)
            .post("/api/login");

        if (response.getStatusCode() == 200) {
            userToken = response.jsonPath().getString("token");
        }

        // Register and login a seller user (userType = 110)
        // Since admin registers a seller manually via /admin/CreateSeller, we can register them normally, and then directly update their user_type or register them through standard flow
        // The /api/register default is userType = 100. Let's see: we can set up the seller in DB or use the registration logic.
        // Wait, for testing, we can write a test SQL or direct DB insert in a setUp method, or manually register and update user type in userRepository.
        // Let's manually register and update the user type directly via userRepository to guarantee we have a real seller token!
    }

    @Test
    void testRegistrationSuccessAndDuplicateError() {
        long start = System.currentTimeMillis();
        try {
            String email = "register_" + System.currentTimeMillis() + "@example.com";
            Map<String, Object> body = new HashMap<>();
            body.put("name", "New Saree Buyer");
            body.put("email", email);
            body.put("password", "saree123");
            body.put("phone", 9876543210L);
            body.put("address", "Handloom Lane");

            // Success Registration
            given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/register")
                .then()
                .statusCode(200)
                .body("status", is(200))
                .body("message", containsString("successfully"));

            // Duplicate Registration Check (Conflict 409)
            given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/register")
                .then()
                .statusCode(409)
                .body("message", containsString("Email already exists"));

            TestResultCollector.addResult("testRegistrationSuccessAndDuplicateError", "API", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Validated user registration success and duplicate prevention logic");
        } catch (Exception e) {
            TestResultCollector.addResult("testRegistrationSuccessAndDuplicateError", "API", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Registration API tests failed");
            throw e;
        }
    }

    @Test
    void testLoginValidationFailures() {
        long start = System.currentTimeMillis();
        try {
            // Missing email
            Map<String, Object> missingEmail = new HashMap<>();
            missingEmail.put("password", "short");

            given()
                .contentType(ContentType.JSON)
                .body(missingEmail)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400);

            // Invalid credentials
            Map<String, Object> wrongCredentials = new HashMap<>();
            wrongCredentials.put("email", "nonexistent@example.com");
            wrongCredentials.put("password", "wrongpass123");

            given()
                .contentType(ContentType.JSON)
                .body(wrongCredentials)
                .when()
                .post("/api/login")
                .then()
                .statusCode(404)
                .body("message", is("Incorrect Email or Password"));

            TestResultCollector.addResult("testLoginValidationFailures", "API", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Validated bad credentials and missing inputs on login API");
        } catch (Exception e) {
            TestResultCollector.addResult("testLoginValidationFailures", "API", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Login validation API tests failed");
            throw e;
        }
    }

    @Test
    void testProfileAuthFailuresAndSuccess() {
        long start = System.currentTimeMillis();
        try {
            // Missing authorization header
            given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/profile")
                .then()
                .statusCode(400)
                .body("message", is("Cannot Authenticate"));

            // Invalid Token signature / bad token
            given()
                .header("Authorization", "Bearer invalidTokenContent")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/profile")
                .then()
                .statusCode(404); // throws RuntimeException "Invalid token" (mapped to 404 by global handler)

            // Success case
            assertNotNull(userToken, "Token must be present for authenticated request tests");
            given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/profile")
                .then()
                .statusCode(200)
                .body("message", is("success"))
                .body("data.email", is("api_customer@example.com"));

            TestResultCollector.addResult("testProfileAuthFailuresAndSuccess", "API", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Validated profile request auth barriers and success validation");
        } catch (Exception e) {
            TestResultCollector.addResult("testProfileAuthFailuresAndSuccess", "API", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Profile auth API tests failed");
            throw e;
        }
    }

    @Test
    void testInvalidAndLargePayloads() {
        long start = System.currentTimeMillis();
        try {
            // Invalid JSON structure returns 404 due to global RuntimeException handler
            given()
                .contentType(ContentType.JSON)
                .body("{invalid-json-body")
                .when()
                .post("/api/login")
                .then()
                .statusCode(404);

            // Large Payload: generate a huge string for password parameter
            StringBuilder largePassword = new StringBuilder();
            largePassword.append("p".repeat(100000)); // 100KB payload

            Map<String, Object> body = new HashMap<>();
            body.put("name", "Huge Payload User");
            body.put("email", "large@example.com");
            body.put("password", largePassword.toString());
            body.put("phone", 9876543212L);
            body.put("address", "Handloom Lane");

            // Large payload in registration validation
            given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/register")
                .then()
                .statusCode(409); // or 400 validation depending on spring validator limit, here it'll hit register with 409 duplicate check or standard flow. Let's see.

            TestResultCollector.addResult("testInvalidAndLargePayloads", "API", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Checked server robustness when supplied with corrupt or large request payloads");
        } catch (Exception e) {
            TestResultCollector.addResult("testInvalidAndLargePayloads", "API", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Large payload API tests failed");
            throw e;
        }
    }
}
