package com.saveetha.kanchi_wave_hub.qa.unit;

import com.saveetha.kanchi_wave_hub.data.ProductDTO;
import com.saveetha.kanchi_wave_hub.model.Product;
import com.saveetha.kanchi_wave_hub.model.ProductImage;
import com.saveetha.kanchi_wave_hub.repository.ProductImageRepository;
import com.saveetha.kanchi_wave_hub.repository.ProductRepository;
import com.saveetha.kanchi_wave_hub.service.ProductService;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductImageRepository imageRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setProduct_name("Banarasi Saree");
        product.setProduct_description("Pure silk gold zari saree");
        product.setProduct_mrp(5000);
        product.setProduct_offer(10);
        product.setProduct_price(4500);
        product.setSeller_id(10);
    }

    @Test
    void testGetSingleProduct() {
        long start = System.currentTimeMillis();
        try {
            when(repository.getReferenceById(1)).thenReturn(product);

            Product result = productService.getSingleProduct(1);
            assertNotNull(result);
            assertEquals("Banarasi Saree", result.getProduct_name());

            TestResultCollector.addResult("testGetSingleProduct", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Successfully fetched single product");
        } catch (Exception e) {
            TestResultCollector.addResult("testGetSingleProduct", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Single product unit test failed");
            fail(e);
        }
    }

    @Test
    void testGetProductsUsingUserId() {
        long start = System.currentTimeMillis();
        try {
            List<Product> products = new ArrayList<>();
            products.add(product);
            Pageable pageable = PageRequest.of(0, 5);
            when(repository.findBySellerId(10, pageable)).thenReturn(products);

            List<Product> result = productService.getProductsUsingUserId(10, 0, 5);
            assertEquals(1, result.size());
            assertEquals("Banarasi Saree", result.get(0).getProduct_name());

            TestResultCollector.addResult("testGetProductsUsingUserId", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Fetched products for a specific seller ID");
        } catch (Exception e) {
            TestResultCollector.addResult("testGetProductsUsingUserId", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Products by seller unit test failed");
            fail(e);
        }
    }

    @Test
    void testGetProductsWithImagesBySellerId() {
        long start = System.currentTimeMillis();
        try {
            List<Product> products = new ArrayList<>();
            products.add(product);
            Pageable pageable = PageRequest.of(0, 5);
            when(repository.findBySellerId(10, pageable)).thenReturn(products);

            List<ProductImage> images = new ArrayList<>();
            ProductImage img = new ProductImage();
            img.setImageName("saree.jpg");
            img.setProduct(product);
            java.lang.reflect.Field idField = ProductImage.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(img, 1);
            images.add(img);
            when(imageRepository.findByProductId(1)).thenReturn(images);

            List<ProductDTO> result = productService.getProductsWithImagesBySellerId(10, 0, 5);
            assertEquals(1, result.size());
            assertEquals("Banarasi Saree", result.get(0).getProductName());
            assertEquals(1, result.get(0).getProductImages().size());
            assertTrue(result.get(0).getProductImages().get(0).getImageName().contains("saree.jpg"));

            TestResultCollector.addResult("testGetProductsWithImagesBySellerId", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Successfully fetched seller product DTOs with mapped images");
        } catch (Exception e) {
            TestResultCollector.addResult("testGetProductsWithImagesBySellerId", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Seller products with images unit test failed");
            fail(e);
        }
    }

    @Test
    void testGetProductForUser() {
        long start = System.currentTimeMillis();
        try {
            List<Product> list = new ArrayList<>();
            list.add(product);
            Page<Product> page = new PageImpl<>(list);
            Pageable pageable = PageRequest.of(0, 5);
            when(repository.findAll(pageable)).thenReturn(page);

            List<ProductImage> images = new ArrayList<>();
            ProductImage img = new ProductImage();
            img.setImageName("saree.jpg");
            img.setProduct(product);
            java.lang.reflect.Field idField = ProductImage.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(img, 1);
            images.add(img);
            when(imageRepository.findByProductId(1)).thenReturn(images);

            List<ProductDTO> result = productService.getProductForUser(0, 5);
            assertEquals(1, result.size());
            assertEquals("Banarasi Saree", result.get(0).getProductName());

            TestResultCollector.addResult("testGetProductForUser", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Fetched customer-facing product details with image mapping");
        } catch (Exception e) {
            TestResultCollector.addResult("testGetProductForUser", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Products for user unit test failed");
            fail(e);
        }
    }

    @Test
    void testSaveProductWithImages() {
        long start = System.currentTimeMillis();
        try {
            when(repository.save(any(Product.class))).thenReturn(product);

            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

            List<MultipartFile> files = new ArrayList<>();
            files.add(mockFile);

            productService.saveProductWithImages(product, files);

            verify(repository, times(1)).save(product);
            verify(imageRepository, times(1)).saveAll(anyList());

            // Clean up written test images
            String path = System.getProperty("user.dir") + File.separator + "product_image";
            File dir = new File(path);
            if (dir.exists()) {
                File[] fileList = dir.listFiles();
                if (fileList != null) {
                    for (File f : fileList) {
                        if (f.getName().contains("_1_test.jpg")) {
                            f.delete();
                        }
                    }
                }
            }

            TestResultCollector.addResult("testSaveProductWithImages", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Verified multipart saving of product catalog item with image files");
        } catch (Exception e) {
            TestResultCollector.addResult("testSaveProductWithImages", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Save product with images unit test failed");
            fail(e);
        }
    }
}
