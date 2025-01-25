package com.saveetha.kanchi_wave_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.saveetha.kanchi_wave_hub.model.payment;

import jakarta.transaction.Transactional;

@Repository
public interface paymentRepository extends JpaRepository<payment, Integer>{

    List<payment> findBySelleridAndStatus(Integer sellerId, String status);

    // List<payment> findBySelleridAndStatus(Integer sellerId, String status);
    List<payment> findByOrderId(Integer orderId);

    @Modifying
    @Transactional
    @Query("UPDATE payment p SET p.status = :status WHERE p.orderId = :id and p.sellerid = :sellerId")
    int updateOrder(int id, String status, int sellerId);

    
}
