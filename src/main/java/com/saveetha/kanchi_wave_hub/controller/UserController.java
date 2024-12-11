package com.saveetha.kanchi_wave_hub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saveetha.kanchi_wave_hub.dto.UserDTO;
import com.saveetha.kanchi_wave_hub.response.ApiResponse;
import com.saveetha.kanchi_wave_hub.service.UserService;

@RestController
@RequestMapping("/register")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDTO user) {
        userService.saveUser(user);
        ApiResponse response = new ApiResponse(200, "User registered successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
