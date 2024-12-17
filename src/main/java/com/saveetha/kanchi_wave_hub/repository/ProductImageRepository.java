package com.saveetha.kanchi_wave_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saveetha.kanchi_wave_hub.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>  {

    List<ProductImage> findByProductId(int productId);

} 
