package com.saveetha.kanchi_wave_hub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    
    private String transactionId;
    private double amount;
    private String paymentDate;
    private String paymentMethod;
    private String status;
    private int quantity;
    private int userid;
    private int sellerid;
    @Column(name = "productid")
    private int productId;
    private String address;
    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getOrderId() {
        return orderId;
    }
    public String getaddress() {
        return address;
    }
    public void setaddress(String address) {
        this.address = address;
    }


    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUserId() {
        return userid;
    }

    public void setuserid(int userid) {
        this.userid = userid;
    }

    public int getsellerid() {
        return sellerid;
    }

    public void setsellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productid) {
        this.productId = productid;
    }
}



