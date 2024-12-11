package com.saveetha.kanchi_wave_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saveetha.kanchi_wave_hub.dto.UserDTO;

@Repository
public interface UserRepository extends JpaRepository<UserDTO, Long> {

}