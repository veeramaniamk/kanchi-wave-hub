package com.saveetha.kanchi_wave_hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    public boolean checkMailExist(Users user) {
        return userRepository.existsByEmail(user.getEmail());
    }

    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Users getUserProfile(Integer userId) {
        return userRepository.getReferenceById(userId);
    }

    public Users updateUserProfile(Integer userId, Users updatedUser) {
        Users existingUser = userRepository.getReferenceById(userId);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setPhone(updatedUser.getPhone());
            return userRepository.save(existingUser);
        }
        return null;
    }
}
