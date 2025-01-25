package com.saveetha.kanchi_wave_hub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_image")
public class ProductImage2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageName;


    @Column(name = "product_id")
    private Integer productId;

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getId() {
        return id;
    }

    // public void setImageName(String imageName) {
    //     this.image_name = imageName;
    // }

    // public String getImageName() {
    //     return image_name;
    // }

}
