package com.saveetha.kanchi_wave_hub.qa.integration;

import com.saveetha.kanchi_wave_hub.data.ProductDTO;
import com.saveetha.kanchi_wave_hub.model.Product;
import com.saveetha.kanchi_wave_hub.repository.ProductImageRepository;
import com.saveetha.kanchi_wave_hub.repository.ProductRepository;
import com.saveetha.kanchi_wave_hub.service.ProductService;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @BeforeEach
    void setUp() {
        productImageRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void testProductCreationAndCatalogRetrieval() {
        long start = System.currentTimeMillis();
        try {
            // 1. Setup product
            Product prod = new Product();
            prod.setProduct_name("Kanjeevaram Silk");
            prod.setProduct_description("Traditional pure gold zari border");
            prod.setProduct_mrp(15000);
            prod.setProduct_offer(15);
            prod.setProduct_price(12750);
            prod.setSeller_id(110);

            // 2. Prepare mock files
            MockMultipartFile file1 = new MockMultipartFile("product_images", "saree1.jpg", "image/jpeg", new byte[]{1,2,3});
            MockMultipartFile file2 = new MockMultipartFile("product_images", "saree2.jpg", "image/jpeg", new byte[]{4,5,6});
            List<MultipartFile> files = new ArrayList<>();
            files.add(file1);
            files.add(file2);

            // 3. Save product with images
            productService.saveProductWithImages(prod, files);

            // 4. Verify product created
            List<Product> list = productRepository.findBySellerId(110, org.springframework.data.domain.PageRequest.of(0, 10));
            assertEquals(1, list.size());
            Product savedProd = list.get(0);
            assertEquals("Kanjeevaram Silk", savedProd.getProduct_name());

            // 5. Fetch with images by seller
            List<ProductDTO> sellerDTOs = productService.getProductsWithImagesBySellerId(110, 0, 10);
            assertEquals(1, sellerDTOs.size());
            assertEquals(2, sellerDTOs.get(0).getProductImages().size());

            // 6. Fetch for customer catalog
            List<ProductDTO> catalog = productService.getProductForUser(0, 10);
            assertFalse(catalog.isEmpty());

            // Clean up files written to file system
            String path = System.getProperty("user.dir") + File.separator + "product_image";
            File dir = new File(path);
            if (dir.exists()) {
                File[] fileList = dir.listFiles();
                if (fileList != null) {
                    for (File f : fileList) {
                        if (f.getName().contains("saree1.jpg") || f.getName().contains("saree2.jpg")) {
                            f.delete();
                        }
                    }
                }
            }

            TestResultCollector.addResult("testProductCreationAndCatalogRetrieval", "Integration", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Successfully validated product creation and catalog mapping with real file persistence triggers");
        } catch (Exception e) {
            TestResultCollector.addResult("testProductCreationAndCatalogRetrieval", "Integration", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Product lifecycle integration test failed");
            fail(e);
        }
    }
}
