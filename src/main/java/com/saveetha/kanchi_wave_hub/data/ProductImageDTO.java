package com.saveetha.kanchi_wave_hub.data;

public class ProductImageDTO {

    private int id;
    private String imageName;

    // Constructors, Getters and Setters
    public ProductImageDTO(int id, String imageName) {
        this.id = id;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
