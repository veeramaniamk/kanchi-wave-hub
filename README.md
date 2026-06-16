# Kanchi Wave Hub - QA Automation & Performance Testing Framework

This repository includes a complete enterprise-grade testing framework and CI/CD validation pipeline built for Spring Boot 3.x, Java 21, and MySQL 8.

---

## 🛠️ Testing Stack & Architecture

- **Unit Testing**: JUnit 5, Mockito (covers all service classes and utilities, target: > 80% coverage).
- **Integration Testing**: SpringBootTest, Testcontainers MySQL (simulates isolated database interactions).
- **API Testing**: REST Assured (validates successes, edge cases, input validation, and large payloads).
- **Security Testing**: Custom vulnerability scripts validating Role-Based Access (RBAC), JWT manipulation, SQL Injection attempts, Cross-Site Scripting (XSS), Path Traversal blocks, and API Rate Limiting.
- **Database Testing**: Index validation, SQL Query execution plan generation (`EXPLAIN`), N+1 query diagnostics, and transaction rollbacks.
- **Load / Stress Testing**: k6 scripts simulating concurrent traffic tiers (50 to 5000+ VUs) to detect latency boundaries, memory leaks, and horizontal scaling limits.
- **Unified Excel Reporting**: Built using Apache POI, consolidating execution results from JUnit, JaCoCo, database audits, and k6 JSON telemetry into a styled spreadsheet output at `target/reports/TestResults.xlsx`.

---

## 🚀 Getting Started

### Prerequisites

To run the full suite locally, make sure you have:
1. **Java Development Kit (JDK) 21** installed.
2. **Maven 3.8+** installed.
3. **Docker Desktop** (required for Testcontainers and MySQL performance containers).
4. **k6** load testing tool installed.
   - *Windows*: `choco install k6` or download from official releases.
   - *Linux*: `sudo apt-get install k6` or `yum install k6`.
   - *Mac*: `brew install k6`.

---

## 🏃 Run Test Suites

### 1. Functional Tests (Unit + Integration + Security + Database)
Run the entire JUnit 5 test suite:
```bash
mvn clean test
```
This command automatically:
- Starts MySQL in Testcontainers for integration tests.
- Executes service mock unit tests.
- Validates security filters (rate limits, inputs, path traversal).
- Asserts database metrics, EXPLAIN query planning, and N+1 query thresholds.
- Generates the JaCoCo coverage XML reports inside `target/site/jacoco/`.

### 2. Start Application for Performance Testing
To execute k6 performance scripts, start the application locally with a high rate limit configuration (so k6 traffic isn't blocked by the API rate limiter):
```bash
# Set environment datasource to local MySQL or test profile
# Then boot up the application with a high rate-limit override:
mvn spring-boot:run -Dspring-boot.run.arguments="--app.rate-limit=100000"
```
Make sure the server is healthy at: `http://localhost:8080/actuator/health`

### 3. Run Performance Tests (k6)
In another terminal, execute the k6 scripts to collect telemetry:
```bash
# 1. Run Load Test (50 -> 5000 users)
k6 run --summary-export=target/reports/k6-load-summary.json k6/load-test.js

# 2. Run Stress Test (Ramping up to breaking limit)
k6 run --summary-export=target/reports/k6-stress-summary.json k6/stress-test.js

# 3. Run Scalability Test (Spikes & connections pools)
k6 run --summary-export=target/reports/k6-scalability-summary.json k6/scalability-test.js

# 4. Run Reliability / Endurance Test (Simulate traffic longevity)
# Standard validation run:
k6 run --summary-export=target/reports/k6-reliability-summary.json k6/reliability-test.js
# Full 6-hour endurance run:
DURATION=6h k6 run --summary-export=target/reports/k6-reliability-summary.json k6/reliability-test.js
```

### 4. Compile Unified Excel Report
Run the report generator test to merge JUnit XML, JaCoCo XML, and k6 JSON stats into a single Excel file:
```bash
mvn test -Dtest=ExcelReportGeneratorTest
```
Output location:
`target/reports/TestResults.xlsx`

---

## 📊 Consolidated Excel Report Structure

The generated spreadsheet contains structured tabs for diagnostic mapping:
1. **Summary**: Total tests run, pass/fail status, Quality Gate verdicts, and system recommendations.
2. **Unit Tests**: Full listing of all service/utility mocks.
3. **Integration Tests**: Database component interactions.
4. **API Tests**: Endpoint verification and validation thresholds.
5. **Security Tests**: SQLi, XSS, rate limits, JWT, and traversal results.
6. **Load Tests**: Concurrency latency, throughput, and error metrics.
7. **Stress Tests**: Breaking point concurrency thresholds.
8. **Scalability Tests**: Connection pools and horizontal scalability statistics.
9. **Database Tests**: EXPLAIN results, missing index validations, and N+1 diagnostic alerts.
10. **Reliability Tests**: GC, memory, thread, and connection leak status.

---

## 🏆 Pull Request Quality Gate Rules

The CI/CD pipeline enforces the following build rules. If any rule is breached, the build is marked as failed:
- **Code Coverage**: Must be at least **80%** (via JaCoCo).
- **Security Vulnerabilities**: **0** failures allowed (no SQLi, XSS, or JWT bypass issues).
- **API Reliability**: Error rate must be **<= 1.00%** under load.
- **Latency SLAs**: P95 response time must remain **< 500 ms**.

---

## ⛓️ CI/CD GitHub Actions Pipeline

The pipeline is configured in `.github/workflows/qa-pipeline.yml`. It automatically executes on every commit and PR:
1. Checkouts codebase.
2. Sets up JDK 21.
3. Spins up a MySQL 8.0 container service.
4. Compiles, packages, and runs all Maven tests.
5. Launches the application and runs k6 performance suites.
6. Combines JaCoCo, surefire, and k6 logs into the Excel workbook.
7. Asserts the Quality Gate rules.
8. Uploads `TestResults.xlsx`, JaCoCo reports, and execution logs as build artifacts.