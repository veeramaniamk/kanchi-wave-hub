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

    @PostMapping("/create-seller")
public ResponseEntity<ApiResponse> createSeller(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody @Valid Users seller, 
        BindingResult result) {

    // Validate the authorization header
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        return new ResponseEntity<>(getResponse(401, "Missing or invalid Authorization header"), HttpStatus.UNAUTHORIZED);
    }

    String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
    try {
        Integer adminId = jwtUtil.extractUserId(token); // Extract user ID from token
        Users admin = userService.getUserProfile(adminId);

        // Check if the user is an admin
        if (admin == null || admin.getUserType() != 111) { // Assuming 100 represents 'admin'
            return new ResponseEntity<>(getResponse(403, "Only admins can create sellers"), HttpStatus.FORBIDDEN);
        }

        // Check for validation errors in the seller object
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                return new ResponseEntity<>(getResponse(400, error.getDefaultMessage()), HttpStatus.BAD_REQUEST);
            }
        }

        // Check if the email already exists
        if (userService.checkMailExist(seller)) {
            return new ResponseEntity<>(getResponse(409, "Email already exists"), HttpStatus.CONFLICT);
        }

        // Set seller-specific properties
        seller.setUserType(101); // Assuming 110 represents 'seller'
        userService.saveUser(seller);

        return new ResponseEntity<>(getResponse(200, "Seller account created successfully!"), HttpStatus.OK);

    } catch (ExpiredJwtException e) {
        return new ResponseEntity<>(getResponse(401, "Token expired"), HttpStatus.UNAUTHORIZED);
    } catch (SignatureException e) {
        return new ResponseEntity<>(getResponse(401, "Invalid token signature"), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
        return new ResponseEntity<>(getResponse(500, "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("status", 200);
            response.put("message", jwtUtil.extractUserId("Empty Header"));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Token missing or invalid
        }
        String token   = authorizationHeader.substring(7);  // Remove "Bearer " prefix
        Users user = null;
        try {
        Integer userId = jwtUtil.extractUserId(token);
        user = userService.getUserProfile(userId);
        } catch (ExpiredJwtException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            // TODO: handle exception
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // User not found
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
       }
        
        if (user != null) {
            response.put("status", 200);
            response.put("message", "success");
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("address", user.getAddress());
            data.put("profile_image", user.getProfileImage());
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", 404);
            response.put("message", "user not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);  // User not found
        }
    }

}

