package com.saveetha.kanchi_wave_hub.data;

import java.util.List;

public class ProductDTO {

    private int id;
    private int sellerId;
    private String productName;
    private String productDescription;
    private int productMrp;
    private int productOffer;
    private int productPrice;
    private List<ProductImageDTO> productImages;  // List of product image DTOs

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getProductMrp() {
        return productMrp;
    }

    public void setProductMrp(int productMrp) {
        this.productMrp = productMrp;
    }

    public int getProductOffer() {
        return productOffer;
    }

    public void setProductOffer(int productOffer) {
        this.productOffer = productOffer;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public List<ProductImageDTO> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImageDTO> productImages) {
        this.productImages = productImages;
    }
}
