package com.saveetha.kanchi_wave_hub.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saveetha.kanchi_wave_hub.component.JwtUtil;
import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {
   
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping("/CreateSeller")
    public ResponseEntity<Map<String, Object>> createSeller(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Valid Users seller,BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("status", 200);
            response.put("message", jwtUtil.extractUserId("Empty Header"));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Token missing or invalid
        }

        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                response.put("status", 400);
                response.put("message", error.getDefaultMessage());
                if(error.getField().equals("name") || error.getField().equals("phone") || error.getField().equals("address")) {
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }

        String token   = authorizationHeader.substring(7);  // Remove "Bearer " prefix
        Users users = null;
        try {
          Integer userId = jwtUtil.extractUserId(token);
          users = userService.updateUserProfile(userId, seller);
        } catch (ExpiredJwtException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("from", e.getClass().getName());
            // TODO: handle exception
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // User not found
        } catch (SignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + " Exception", e);
       }
       if(userService.checkMailExist(seller)) {
        response.put("status", 409);
          response.put("message", "email already exist");
        return  new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    seller.setUserType(110);
    userService.saveUser(seller);
          response.put("status", 200);
          response.put("message", "account craeted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
