package com.saveetha.kanchi_wave_hub.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.saveetha.kanchi_wave_hub.component.JwtUtil;
import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.response.ApiResponse;
import com.saveetha.kanchi_wave_hub.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private ApiResponse getResponse(int status, String msg) {
        return new ApiResponse(status, msg);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createUser(@RequestBody Users user) {
        if(userService.checkMailExist(user)) {
            return new ResponseEntity<>(getResponse(409, "Email already exists"), HttpStatus.CONFLICT);
        }
        user.setUserType(100);
        userService.saveUser(user);
        return new ResponseEntity<>(getResponse(200, "User registered successfully!"), HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid Users user, BindingResult result) {
        Users existingUser = userService.findByEmail(user.getEmail());
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                response.put("status", 400);
                // error.getObjectName()
                response.put("message", error.getDefaultMessage());
                if(error.getField().equals("email") || error.getField().equals("password")) {
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }
        
        if (existingUser == null || !user.getPassword().equals(existingUser.getPassword())) {
            response.put("status", 404);
            response.put("message", "Incorrect Email or Password");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("status", 200);
        response.put("message", "Login Success");
        String token = jwtUtil.generateToken(existingUser);
        response.put("token", token);
        response.put("name", existingUser.getName());
        response.put("userType", existingUser.getUserType());
        response.put("profile", existingUser.getProfileImage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> decodeToken(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Valid Users user, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Empty Header");
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
          users = userService.updateUserProfile(userId, user);
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
       if(users==null) {
          response.put("status", 404);
          response.put("message", "User Not Found");
          return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
          response.put("status", 200);
          response.put("message", "User Profile Updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    @PutMapping("/profileImage")
    public ResponseEntity<Map<String, Object>> updateProfileImage(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("status", 200);
            response.put("message", jwtUtil.extractUserId("Empty Header"));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Token missing or invalid
        }
        String token   = authorizationHeader.substring(7);  // Remove "Bearer " prefix
        Integer userId;
        try {
        userId = jwtUtil.extractUserId(token);
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

        String filename = "";
        try {
            filename = saveImage(file);
        }catch (IOException e) {
            response.put("status", 500);
            response.put("message", "File Upload Error "+ e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        // response.put("current dir", filename);

        userService.upateProfileImage(userId, filename); 
        response.put("status", 200);
        response.put("message", "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    private String saveImage(MultipartFile file) throws IOException {

        String path = System.getProperty("user.dir") + File.separator;
    //    String path = System.getProperty("java.io.tmpdir");
    //    return path;
        File dir = new File(path, "profile_image");

        
        if(!dir.exists()) dir.mkdir();

        LocalDate currentDate = LocalDate.now();   
        Path uploadPath = Paths.get(dir.getPath());
        String fileName = currentDate + "-"+Calendar.getInstance().get(Calendar.MILLISECOND) + file.getOriginalFilename();
        Path filePath   = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;

        // return path;
    }

}
