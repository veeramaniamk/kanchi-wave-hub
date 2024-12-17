package com.saveetha.kanchi_wave_hub.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saveetha.kanchi_wave_hub.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product getReferenceById(Integer id);

    // List<Product> findAll(Integer seller_id);

    // List<Product> findAllById(Integer seller_id);

    List<Product> findBySellerId(int sellerId, Pageable pageable);


}
