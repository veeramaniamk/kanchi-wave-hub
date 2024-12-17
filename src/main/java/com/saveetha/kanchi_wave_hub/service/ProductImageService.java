package com.saveetha.kanchi_wave_hub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saveetha.kanchi_wave_hub.model.ProductImage;
import com.saveetha.kanchi_wave_hub.repository.ProductImageRepository;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository repository;

    public List<ProductImage> getProductImages(Integer productId) {
        return repository.findByProductId(productId);
    }

}
