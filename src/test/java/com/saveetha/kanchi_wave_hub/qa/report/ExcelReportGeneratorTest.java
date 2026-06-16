package com.saveetha.kanchi_wave_hub.qa.report;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcelReportGeneratorTest {

    @Test
    void testGenerateConsolidatedReport() {
        // Run Excel Generation
        ExcelReportGenerator.generateReport();

        // Verify output file exists
        File report = new File("target/reports/TestResults.xlsx");
        assertTrue(report.exists(), "The consolidated test report Excel file must be successfully generated at 'target/reports/TestResults.xlsx'");
    }
}
