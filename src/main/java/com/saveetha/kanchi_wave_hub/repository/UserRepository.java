package com.saveetha.kanchi_wave_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saveetha.kanchi_wave_hub.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    boolean existsByEmail(String email);

    Users findByEmail(String email);

    // @Query("SELECT u.id, u.email, u.phone, u.address, u.profile_image FROM Users u WHERE u.id = :id")
    // @Query("SELECT new com.saveetha.kanchi_wave_hub.response.UserDataClass(u.id, u.email, u.phone, u.address, u.profile_image) FROM Users u WHERE u.id = :id")
    Users getReferenceById(Integer id);

}