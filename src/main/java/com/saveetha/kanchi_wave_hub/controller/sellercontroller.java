package com.saveetha.kanchi_wave_hub.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.saveetha.kanchi_wave_hub.component.JwtUtil;
import com.saveetha.kanchi_wave_hub.data.ProductDTO;
import com.saveetha.kanchi_wave_hub.model.Product;
import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.service.ProductImageService;
import com.saveetha.kanchi_wave_hub.service.ProductService;
import com.saveetha.kanchi_wave_hub.service.UserService;
import com.saveetha.kanchi_wave_hub.service.paymentService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class sellercontroller {

    @Autowired
    private ProductService sellService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductImageService imageService;

    @Autowired
    private paymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/create_product", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createProduct(
        @RequestHeader("Authorization") String header, 
        @RequestPart String productName, @RequestPart String product_description,
        @RequestPart String product_mrp, @RequestPart String product_offer,
        @RequestPart String product_price, @RequestPart("product_images") List<MultipartFile> files) {
        Map<String, Object> response = new HashMap<>();
        if (header == null || !header.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Empty Header");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Token missing or invalid
        }

        if(files.isEmpty()) {
            response.put("status", 400);
            response.put("message", "Select Images");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String token = header.substring(7);

        try {
            Integer userId = jwtUtil.extractUserId(token);

            Users userData = userService.getUserProfile(userId);

            if(userData.getUserType() != 110) {
                response.put("status", 403);
                response.put("message", "Access Denied ");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            Product product = new Product();

            product.setProduct_name(productName);
            product.setProduct_description(product_description);
            product.setProduct_mrp(Integer.parseInt(product_mrp));
            product.setProduct_offer(Integer.parseInt(product_offer));
            product.setProduct_price(Integer.parseInt(product_price));
            product.setSeller_id(userId);

            try {
                sellService.saveProductWithImages(product, files);
            } catch (IOException ex) {
                response.put("status", 500);
                response.put("message", ex.getMessage());
                response.put("from", "IO exception");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            response.put("status", 200);
            response.put("message", "Product Created");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } 
        catch (ExpiredJwtException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("from", e.getClass().getName());
            // TODO: handle exception
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // User not found
        } 
        catch (SignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
        } 
    }

    @GetMapping("/fetch_product")
    public ResponseEntity<Map<String, Object>> getProduct(@RequestHeader("Authorization") String header,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Map<String, Object> response = new HashMap<>();
        if (header == null || !header.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Empty Header");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Token missing or invalid
        }

        String token = header.substring(7);

        try {
            Integer userId = jwtUtil.extractUserId(token);

            Users userData = userService.getUserProfile(userId);

            if(userData.getUserType() != 110) {
                response.put("status", 403);
                response.put("message", "Access Denied ");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            response.put("status", 200);
            response.put("message", "Success");
            response.put("data", sellService.getProductsWithImagesBySellerId(userId, page, size));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } 
        catch (ExpiredJwtException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("from", e.getClass().getName());
            // TODO: handle exception
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // User not found
        } 
        catch (SignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
        } 

        // return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<byte[]> serveImage(@PathVariable String imageName) {
        try {
            String path = System.getProperty("user.dir") + File.separator + "product_image";
            Path imagePath = Paths.get(path, imageName);
            byte[] imageData = Files.readAllBytes(imagePath);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } 
    }

    @GetMapping("/get_orders")
    public ResponseEntity<Map<String, Object>> getOrders(@RequestHeader("Authorization") String header) {
        Map<String, Object> response = new HashMap<>();
        if (header == null || !header.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Empty Header");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Token missing or invalid
        }

        String token = header.substring(7);
        try {
            Integer userId = jwtUtil.extractUserId(token);

            Users userData = userService.getUserProfile(userId);

            if(userData.getUserType() != 110) {
                response.put("status", 403);
                response.put("message", "Access Denied ");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            if(paymentService.getOrderBySellerId(userId).isEmpty()) {
                response.put("status", 404);
                response.put("message", "Order Not Found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.put("status", 200);
            response.put("message", "Success");
            response.put("data", paymentService.getOrderBySellerId(userId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } 
        catch (ExpiredJwtException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("from", e.getClass().getName());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // User not found
        } 
        catch (SignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        // return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get_single_product/{productId}")
    public ResponseEntity<Map<String, Object>> getMethodName(@PathVariable Integer productId) {
        Map<String, Object> response = new HashMap<>();
        
        if(productId==0) {
            response.put("status", 400);
            response.put("message", "Product Id Missing");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.put("status", 200);
        response.put("message", "Success");
        List<ProductDTO> data = sellService.getProductsWithId(productId);

        if(data.size()==0) {
            response.put("status", 404);
            response.put("message", "Product Not Found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("data", data.get(0));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    

}

