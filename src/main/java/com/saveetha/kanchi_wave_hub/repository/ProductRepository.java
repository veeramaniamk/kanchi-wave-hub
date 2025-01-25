package com.saveetha.kanchi_wave_hub.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saveetha.kanchi_wave_hub.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product getReferenceById(Integer id);
    // List<Product> findAll(Integer id);

    // List<Product> findAll(Integer seller_id);

    // List<Product> findAllById(Integer seller_id);

    List<Product> findBySellerId(int sellerId, Pageable pageable);
    List<Product> findById(int sellerId);

    // List<Product> findAll();

    // List<Product> fetchProducts();

    // @Query("SELECT p.id, p.sellerId, p.product_name, p.product_description," + //
    //             "p.product_mrp , p.product_offer, p.product_price,  pm.id image_id FROM Product p JOIN ProductImage2 pm ON pm.productId = p.id WHERE p.id = :productId")
    // List<Object[]> findProductWithImages(@Param("productId") Long productId);
}
