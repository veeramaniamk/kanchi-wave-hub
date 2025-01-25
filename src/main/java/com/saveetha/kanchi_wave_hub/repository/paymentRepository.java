package com.saveetha.kanchi_wave_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saveetha.kanchi_wave_hub.model.payment;

@Repository
public interface paymentRepository extends JpaRepository<payment, Integer>{

    List<payment> findBySellerid(Integer sellerId);
}
