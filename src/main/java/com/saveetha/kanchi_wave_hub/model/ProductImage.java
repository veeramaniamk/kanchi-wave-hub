package com.saveetha.kanchi_wave_hub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_image")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageName;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // @Column(name = "product_image.product_id")
    // private Integer productId;

    // public void setProductId(Integer productId) {
    //     this.productId = productId;
    // }

    public Integer getId() {
        return id;
    }

    // public Integer getProductId() {
    //     return productId;
    // }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product geProduct(){
        return product;
    }
}
