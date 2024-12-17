package com.saveetha.kanchi_wave_hub.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.saveetha.kanchi_wave_hub.data.ProductDTO;
import com.saveetha.kanchi_wave_hub.data.ProductImageDTO;
import com.saveetha.kanchi_wave_hub.model.Product;
import com.saveetha.kanchi_wave_hub.model.ProductImage;
import com.saveetha.kanchi_wave_hub.repository.ProductImageRepository;
import com.saveetha.kanchi_wave_hub.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductImageRepository imageRepository;

    private static final String IMAGE_BASE_URL = "http://localhost:8080/images/";

    public void saveProductWithImages(Product product, List<MultipartFile> images) throws IOException {

        Product savedProduct = repository.save(product);

        String path = System.getProperty("user.dir") + File.separator;
    //    String path = System.getProperty("java.io.tmpdir");
    //    return path;
        File dir = new File(path, "product_image");
        
        if(!dir.exists()) dir.mkdir();

        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile image : images) {
            LocalDate currentDate = LocalDate.now();   
            String time = currentDate + "-"+Calendar.getInstance().get(Calendar.MILLISECOND);
            String imageName = time + "_" + savedProduct.getId() + "_" + image.getOriginalFilename();
            // File destinationFile = new File(dir + imageName);
            Path uploadPath = Paths.get(dir.getPath());
            Path filePath   = uploadPath.resolve(imageName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // image.transferTo(destinationFile);

            ProductImage productImage = new ProductImage();
            productImage.setImageName(imageName);
            productImage.setProduct(savedProduct);
            productImages.add(productImage);
        }

       imageRepository.saveAll(productImages);
    }

    public Product getSingleProduct(Integer productId) {
        return repository.getReferenceById(productId);
    }

    public List<Product> getProductsUsingUserId(Integer sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> productData = repository.findBySellerId(sellerId, pageable);
        return productData;
    }

    @Transactional
    public List<ProductDTO> getProductsWithImagesBySellerId(int sellerId, int page, int size) {
        // Fetch products by sellerId
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = repository.findBySellerId(sellerId, pageable);

        // Map products to ProductDTOs and include images with URLs
        return products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setSellerId(product.getSeller_id());
            productDTO.setProductName(product.getProduct_name());
            productDTO.setProductDescription(product.getProduct_description());
            productDTO.setProductMrp(product.getProduct_mrp());
            productDTO.setProductOffer(product.getProduct_offer());
            productDTO.setProductPrice(product.getProduct_price());

            // Fetch images for the product
            List<ProductImageDTO> productImages = imageRepository.findByProductId(product.getId()).stream()
                    .map(image -> new ProductImageDTO(
                            image.getId(),
                            IMAGE_BASE_URL + image.getImageName()  // Create the full image URL
                    ))
                    .collect(Collectors.toList());

            productDTO.setProductImages(productImages);
            return productDTO;
        }).collect(Collectors.toList());
    }

}
