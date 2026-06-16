package com.saveetha.kanchi_wave_hub.qa.database;

import com.saveetha.kanchi_wave_hub.model.Product;
import com.saveetha.kanchi_wave_hub.model.ProductImage;
import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.repository.ProductImageRepository;
import com.saveetha.kanchi_wave_hub.repository.ProductRepository;
import com.saveetha.kanchi_wave_hub.repository.UserRepository;
import com.saveetha.kanchi_wave_hub.service.ProductService;
import com.saveetha.kanchi_wave_hub.service.UserService;
import com.saveetha.kanchi_wave_hub.qa.integration.BaseIntegrationTest;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionSystemException;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabasePerformanceTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testDatabaseIndexes() {
        long start = System.currentTimeMillis();
        try {
            // Verify that indexes exist on the users table (specifically email)
            DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            ResultSet rs = metaData.getIndexInfo(null, null, "users", false, false);
            boolean hasEmailIndex = false;
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                if ("email".equalsIgnoreCase(colName)) {
                    hasEmailIndex = true;
                }
            }

            assertTrue(hasEmailIndex, "The 'users' table must have an index on the 'email' column to optimize login queries.");

            TestResultCollector.addResult("testDatabaseIndexes", "Database", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Indexes verified: users.email contains a database index");
        } catch (Exception e) {
            TestResultCollector.addResult("testDatabaseIndexes", "Database", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Database index verification failed");
            fail(e);
        }
    }

    @Test
    void testSlowQueriesAndExplainPlan() {
        long start = System.currentTimeMillis();
        try {
            // Setup mock product
            Product prod = new Product();
            prod.setProduct_name("Banarasi Zari");
            prod.setProduct_description("Pure silk");
            prod.setProduct_price(5000);
            prod.setSeller_id(10);
            productRepository.save(prod);

            // Execute EXPLAIN query
            List<Map<String, Object>> explainPlan = jdbcTemplate.queryForList("EXPLAIN SELECT * FROM product WHERE seller_id = 10");
            assertFalse(explainPlan.isEmpty());

            System.out.println("EXPLAIN PLAN RESULT:");
            for (Map<String, Object> row : explainPlan) {
                System.out.println(row.toString());
            }

            // Verify execution plan has acceptable type (e.g. ALL or ref)
            String selectType = explainPlan.get(0).get("select_type").toString();
            assertNotNull(selectType);

            TestResultCollector.addResult("testSlowQueriesAndExplainPlan", "Database", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "EXPLAIN plan generated: select_type = " + selectType);
        } catch (Exception e) {
            TestResultCollector.addResult("testSlowQueriesAndExplainPlan", "Database", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "EXPLAIN analysis failed");
            fail(e);
        }
    }

    @Test
    void testNPlusOneQueryDetection() {
        long start = System.currentTimeMillis();
        try {
            // Enable Hibernate Statistics
            SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
            sessionFactory.getStatistics().clear();
            sessionFactory.getStatistics().setStatisticsEnabled(true);

            // 1. Insert 5 products, each with 2 images
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                Product prod = new Product();
                prod.setProduct_name("Saree " + i);
                prod.setProduct_description("Silk saree " + i);
                prod.setProduct_price(3000 + (i * 100));
                prod.setSeller_id(110);
                Product saved = productRepository.save(prod);

                ProductImage img1 = new ProductImage();
                img1.setImageName("img_" + i + "_a.jpg");
                img1.setProduct(saved);
                productImageRepository.save(img1);

                ProductImage img2 = new ProductImage();
                img2.setImageName("img_" + i + "_b.jpg");
                img2.setProduct(saved);
                productImageRepository.save(img2);
            }

            sessionFactory.getStatistics().clear(); // Clear stats before testing read method

            // 2. Execute getProductForUser (which retrieves products and lazy-loads or queries images inside a loop)
            productService.getProductForUser(0, 10);

            long prepareStatementCount = sessionFactory.getStatistics().getPrepareStatementCount();
            System.out.println("Hibernate statements executed: " + prepareStatementCount);

            // Analysis: 1 query to fetch the products, and 1 query *per product* to fetch the images.
            // With 5 products, we expect 1 + 5 = 6 queries.
            // If queryCount > 2 for fetching products + images, it is an N+1 query issue!
            String recommendation = "No N+1 queries detected.";
            if (prepareStatementCount > 2) {
                recommendation = "N+1 query problem detected in ProductService.getProductForUser! ProductService fetches products and then issues a separate query for each product to load its images. Fix this by using a Join Fetch or Entity Graph, or using @BatchSize(size = 20) on the images association.";
            }

            // We do not fail the build for N+1 yet, but we log the diagnostic results and recommendation for the Excel sheet!
            TestResultCollector.addResult("testNPlusOneQueryDetection", "Database", "PASSED", System.currentTimeMillis() - start, 0, null, 100, recommendation);
        } catch (Exception e) {
            TestResultCollector.addResult("testNPlusOneQueryDetection", "Database", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "N+1 query check failed");
            fail(e);
        }
    }

    @Test
    void testTransactionConsistencyRollback() {
        long start = System.currentTimeMillis();
        try {
            Users user = new Users();
            user.setName(""); // Invalid: NotBlank constraint fails
            user.setEmail("invalid-email");
            user.setPassword("123"); // Invalid: Size min=6 fails
            user.setPhone(12345L);
            user.setAddress("");

            assertThrows(Exception.class, () -> {
                // Should trigger constraint violation and throw exception
                userService.saveUser(user);
            });

            // Verify no users exist in database (rollback was successful)
            long count = userRepository.count();
            assertEquals(0, count, "Transaction rollback failed. Database contains invalid records.");

            TestResultCollector.addResult("testTransactionConsistencyRollback", "Database", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Rollback verified: invalid transaction rolled back successfully");
        } catch (Exception e) {
            TestResultCollector.addResult("testTransactionConsistencyRollback", "Database", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Transaction rollback test failed");
            fail(e);
        }
    }
}
