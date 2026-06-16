package com.saveetha.kanchi_wave_hub.qa.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.saveetha.kanchi_wave_hub.config.RateLimitingFilter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {

    @Autowired(required = false)
    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    public void resetRateLimiter() {
        if (rateLimitingFilter != null) {
            rateLimitingFilter.reset();
        }
    }

    protected static final MySQLContainer<?> mysqlContainer;

    static {
        MySQLContainer<?> container = null;
        try {
            // Check if Docker is running using standard Testcontainers API
            if (org.testcontainers.DockerClientFactory.instance().isDockerAvailable()) {
                container = new MySQLContainer<>("mysql:8.0.33")
                        .withDatabaseName("kanchi-wave-hub-test")
                        .withUsername("testuser")
                        .withPassword("testpass");
                container.start();
            } else {
                System.err.println("WARNING: Docker is not active. Using fallback default MySQL database connection.");
            }
        } catch (Throwable e) {
            System.err.println("WARNING: Docker environment not found for Testcontainers: " + e.getMessage());
            System.err.println("Falling back to local/default MySQL database at port 3306.");
            container = null;
        }
        mysqlContainer = container;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (mysqlContainer != null && mysqlContainer.isRunning()) {
            registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
            registry.add("spring.datasource.username", mysqlContainer::getUsername);
            registry.add("spring.datasource.password", mysqlContainer::getPassword);
        } else {
            // Graceful fallback to default local database properties
            registry.add("spring.datasource.url", () -> "jdbc:mysql://localhost:3306/kanchi-wave-hub");
            registry.add("spring.datasource.username", () -> "root");
            registry.add("spring.datasource.password", () -> "12345");
        }
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("app.rate-limit", () -> "5");
    }
}
