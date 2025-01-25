package com.saveetha.kanchi_wave_hub.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saveetha.kanchi_wave_hub.data.OrderData;
import com.saveetha.kanchi_wave_hub.model.Product;
import com.saveetha.kanchi_wave_hub.model.ProductImage;
import com.saveetha.kanchi_wave_hub.model.payment;
import com.saveetha.kanchi_wave_hub.repository.ProductImageRepository;
import com.saveetha.kanchi_wave_hub.repository.ProductRepository;
import com.saveetha.kanchi_wave_hub.repository.paymentRepository;

@Service
public class paymentService {
    @Autowired
    private paymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository imageRepository;

    // Method to add a new payment
    public payment addPayment(payment payment) {
        return paymentRepository.save(payment);
    }

    private static final String IMAGE_BASE_URL = "seller/images/";

    public List<OrderData> getOrderBySellerId(int sellerId, String status) {

        List<payment> orderData = paymentRepository.findBySelleridAndStatus(sellerId, status);
        List<OrderData> responseData = new ArrayList();
        orderData.forEach(data-> {
            OrderData orderResponseData = new OrderData();

            Product product = productRepository.getReferenceById(data.getProductId());
            if(product!=null) {
                List<ProductImage> productImages = imageRepository.findByProductId(product.getId());
                orderResponseData.setAddress(data.getaddress());
                orderResponseData.setAmount(data.getAmount());
                orderResponseData.setOrderId(data.getOrderId());
                orderResponseData.setPaymentDate(data.getPaymentDate());
                orderResponseData.setPaymentMethod(data.getPaymentMethod());
                orderResponseData.setTransactionId(data.getTransactionId());
                // orderResponseData.setStatus(data.getStatus());
                orderResponseData.setStatus("pending");
                orderResponseData.setQuantity(data.getQuantity());
                orderResponseData.setSellerId(data.getsellerid());
                orderResponseData.setProductId(data.getProductId());
                orderResponseData.setUserId(data.getUserId());
                orderResponseData.setProductName(product.getProduct_name());
                
                if(productImages.size()>0) {
                    orderResponseData.setProductImage(IMAGE_BASE_URL+productImages.get(0).getImageName());
                } else {
                    orderResponseData.setProductImage(null);
                }
            }
            responseData.add(orderResponseData);
        });
        return responseData;
    }

    public int updateOrer(int orderId, String status, int sellerId) {
        return paymentRepository.updateOrder(orderId, status, sellerId);
    }

}
