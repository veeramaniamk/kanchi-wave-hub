package com.saveetha.kanchi_wave_hub.qa.unit;

import com.saveetha.kanchi_wave_hub.data.OrderData;
import com.saveetha.kanchi_wave_hub.model.Product;
import com.saveetha.kanchi_wave_hub.model.ProductImage;
import com.saveetha.kanchi_wave_hub.model.payment;
import com.saveetha.kanchi_wave_hub.repository.ProductImageRepository;
import com.saveetha.kanchi_wave_hub.repository.ProductRepository;
import com.saveetha.kanchi_wave_hub.repository.paymentRepository;
import com.saveetha.kanchi_wave_hub.service.paymentService;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private paymentRepository payRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository imageRepository;

    @InjectMocks
    private paymentService payService;

    private payment testPayment;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testPayment = new payment();
        testPayment.setOrderId(100);
        testPayment.setTransactionId("TXN12345");
        testPayment.setAmount(4500.0);
        testPayment.setPaymentDate("2026-06-16");
        testPayment.setPaymentMethod("UPI");
        testPayment.setStatus("success");
        testPayment.setQuantity(1);
        testPayment.setuserid(5);
        testPayment.setsellerid(10);
        testPayment.setProductId(1);
        testPayment.setaddress("123 Buyer St");

        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setProduct_name("Banarasi Saree");
        testProduct.setSeller_id(10);
    }

    @Test
    void testAddPayment() {
        long start = System.currentTimeMillis();
        try {
            when(payRepository.save(any(payment.class))).thenReturn(testPayment);

            payment saved = payService.addPayment(testPayment);
            assertNotNull(saved);
            assertEquals("TXN12345", saved.getTransactionId());
            assertEquals(4500.0, saved.getAmount());
            verify(payRepository, times(1)).save(testPayment);

            TestResultCollector.addResult("testAddPayment", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Successfully recorded new payment transactions");
        } catch (Exception e) {
            TestResultCollector.addResult("testAddPayment", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Add payment unit test failed");
            fail(e);
        }
    }

    @Test
    void testGetOrderBySellerId() {
        long start = System.currentTimeMillis();
        try {
            List<payment> payments = new ArrayList<>();
            payments.add(testPayment);
            when(payRepository.findBySelleridAndStatus(10, "success")).thenReturn(payments);
            when(productRepository.getReferenceById(1)).thenReturn(testProduct);

            List<ProductImage> images = new ArrayList<>();
            ProductImage img = new ProductImage();
            img.setImageName("saree.jpg");
            images.add(img);
            when(imageRepository.findByProductId(1)).thenReturn(images);

            List<OrderData> result = payService.getOrderBySellerId(10, "success");
            assertEquals(1, result.size());
            assertEquals("Banarasi Saree", result.get(0).getProductName());
            assertEquals("seller/images/saree.jpg", result.get(0).getProductImage());

            TestResultCollector.addResult("testGetOrderBySellerId", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Fetched orders filtered by seller ID and status");
        } catch (Exception e) {
            TestResultCollector.addResult("testGetOrderBySellerId", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Get orders by seller unit test failed");
            fail(e);
        }
    }

    @Test
    void testGetOrderByUserId() {
        long start = System.currentTimeMillis();
        try {
            List<payment> payments = new ArrayList<>();
            payments.add(testPayment);
            when(payRepository.findByUseridAndStatus(5, "pending")).thenReturn(payments);
            when(productRepository.getReferenceById(1)).thenReturn(testProduct);

            List<ProductImage> images = new ArrayList<>();
            when(imageRepository.findByProductId(1)).thenReturn(images);

            List<OrderData> result = payService.getOrderByUserId(5, "pending");
            assertEquals(1, result.size());
            assertEquals("Banarasi Saree", result.get(0).getProductName());
            assertNull(result.get(0).getProductImage());

            TestResultCollector.addResult("testGetOrderByUserId", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Fetched orders filtered by user ID and status");
        } catch (Exception e) {
            TestResultCollector.addResult("testGetOrderByUserId", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Get orders by user unit test failed");
            fail(e);
        }
    }

    @Test
    void testUpdateOrder() {
        long start = System.currentTimeMillis();
        try {
            when(payRepository.updateOrder(100, "shipped", 10)).thenReturn(1);

            int updated = payService.updateOrer(100, "shipped", 10);
            assertEquals(1, updated);
            verify(payRepository, times(1)).updateOrder(100, "shipped", 10);

            TestResultCollector.addResult("testUpdateOrder", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Updated order shipment and delivery status fields");
        } catch (Exception e) {
            TestResultCollector.addResult("testUpdateOrder", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Update order unit test failed");
            fail(e);
        }
    }
}
