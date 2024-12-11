package com.saveetha.kanchi_wave_hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saveetha.kanchi_wave_hub.dto.UserDTO;
import com.saveetha.kanchi_wave_hub.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO saveUser(UserDTO user) {
        return userRepository.save(user);
    }
}
