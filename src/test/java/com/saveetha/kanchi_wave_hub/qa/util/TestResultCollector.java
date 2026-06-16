package com.saveetha.kanchi_wave_hub.qa.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestResultCollector {

    public static class TestResult {
        private String testName;
        private String testType; // Unit, Integration, API, Security, Database, Load, Stress, Scalability, Reliability
        private String status;   // PASSED, FAILED
        private long durationMs;
        private long responseTimeMs;
        private String errorDetails;
        private double coveragePct;
        private String recommendation;

        public TestResult() {}

        public TestResult(String testName, String testType, String status, long durationMs, long responseTimeMs, String errorDetails, double coveragePct, String recommendation) {
            this.testName = testName;
            this.testType = testType;
            this.status = status;
            this.durationMs = durationMs;
            this.responseTimeMs = responseTimeMs;
            this.errorDetails = errorDetails;
            this.coveragePct = coveragePct;
            this.recommendation = recommendation;
        }

        // Getters and Setters
        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }

        public String getTestType() { return testType; }
        public void setTestType(String testType) { this.testType = testType; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getDurationMs() { return durationMs; }
        public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

        public long getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

        public String getErrorDetails() { return errorDetails; }
        public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }

        public double getCoveragePct() { return coveragePct; }
        public void setCoveragePct(double coveragePct) { this.coveragePct = coveragePct; }

        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }

    private static final List<TestResult> results = Collections.synchronizedList(new ArrayList<>());
    private static final String OUTPUT_DIR = "target/reports";
    private static final String OUTPUT_FILE = "target/reports/test-results-collector.json";

    public static void addResult(String testName, String testType, String status, long durationMs, long responseTimeMs, String errorDetails, double coveragePct, String recommendation) {
        results.add(new TestResult(testName, testType, status, durationMs, responseTimeMs, errorDetails, coveragePct, recommendation));
        saveToFile();
    }

    public static void addResult(TestResult result) {
        results.add(result);
        saveToFile();
    }

    public static List<TestResult> getResults() {
        // Try to load from file if memory list is empty (for cross-JVM or separate test class runs)
        if (results.isEmpty()) {
            loadFromFile();
        }
        return new ArrayList<>(results);
    }

    public static void saveToFile() {
        try {
            File dir = new File(OUTPUT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(OUTPUT_FILE), results);
        } catch (IOException e) {
            System.err.println("Failed to write test results collector to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadFromFile() {
        File file = new File(OUTPUT_FILE);
        if (file.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<TestResult> loaded = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, TestResult.class));
                results.clear();
                results.addAll(loaded);
            } catch (IOException e) {
                System.err.println("Failed to read test results collector from file: " + e.getMessage());
            }
        }
    }
}
