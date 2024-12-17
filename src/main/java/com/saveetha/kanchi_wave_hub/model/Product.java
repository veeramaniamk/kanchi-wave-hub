package com.saveetha.kanchi_wave_hub.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "seller_id")
    private int sellerId;
    
    @NotNull(message = "Enter Product name")
    private String product_name;

    @NotNull(message = "Enter product description")
    private String product_description;

    @NotNull(message = "Enter MRP")
    @Min(value = 1, message = "Enter product mrp")
    private int product_mrp;

    @NotNull(message = "Enter Offer")
    @Min(value = 1, message = "Enter product offer")
    private int product_offer;

    @NotNull(message = "Enter Price")
    @Min(value = 1, message = "Enter product price")
    private int product_price;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages;

    public void setProductImage(List<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public List<ProductImage> gProductImages() {
        return productImages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeller_id() {
        return sellerId;
    }

    public void setSeller_id(int seller_id) {
        this.sellerId = seller_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public int getProduct_mrp() {
        return product_mrp;
    }

    public void setProduct_mrp(int product_mrp) {
        this.product_mrp = product_mrp;
    }

    public int getProduct_offer() {
        return product_offer;
    }

    public void setProduct_offer(int product_offer) {
        this.product_offer = product_offer;
    }

    public int getProduct_price() {
        return product_price;
    }

    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }
    

}
