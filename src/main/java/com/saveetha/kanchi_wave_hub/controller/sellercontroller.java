package com.saveetha.kanchi_wave_hub.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saveetha.kanchi_wave_hub.component.JwtUtil;
import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.response.ApiResponse;
import com.saveetha.kanchi_wave_hub.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/seller")

public class sellercontroller {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private ApiResponse getResponse(int status, String msg) {
        return new ApiResponse(status, msg);
    }

    


}

