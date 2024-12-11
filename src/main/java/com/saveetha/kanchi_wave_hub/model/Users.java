package com.saveetha.kanchi_wave_hub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Enter valid email")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Phone Number must not be blank")
    private Long phone;

    @NotBlank(message = "Address must not be blank")
    private String address;
    
    private String profile_image;

    private Integer user_type;

    public Integer getUserType() {
        return user_type;
    }

    public void setUserType(Integer userType) {
        this.user_type = userType;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProfileImage(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getProfileImage() {
        return profile_image;
    }

    public String getAddress() {
        return address;
    }

    public Long getPhone() {
        return phone;
    }
    // Getters and Setters
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
