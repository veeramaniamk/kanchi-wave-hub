-- Database Schema Initialization for Kanchi Wave Hub (MySQL)
-- Created safely with 'IF NOT EXISTS' for all tables and constraints

CREATE DATABASE IF NOT EXISTS `kanchi-wave-hub`;
USE `kanchi-wave-hub`;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `phone` BIGINT NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `profile_image` VARCHAR(255) DEFAULT NULL,
    `user_type` INT DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `idx_users_email` UNIQUE (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Product Table
CREATE TABLE IF NOT EXISTS `product` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `seller_id` INT DEFAULT NULL,
    `product_name` VARCHAR(255) NOT NULL,
    `product_description` VARCHAR(255) NOT NULL,
    `product_mrp` INT NOT NULL CHECK (`product_mrp` >= 1),
    `product_offer` INT NOT NULL CHECK (`product_offer` >= 1),
    `product_price` INT NOT NULL CHECK (`product_price` >= 1),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Product Image Table
CREATE TABLE IF NOT EXISTS `product_image` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `image_name` VARCHAR(255) DEFAULT NULL,
    `product_id` INT DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_product_image_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Payment Table
CREATE TABLE IF NOT EXISTS `payment` (
    `order_id` INT NOT NULL AUTO_INCREMENT,
    `transaction_id` VARCHAR(255) DEFAULT NULL,
    `amount` DOUBLE NOT NULL,
    `payment_date` VARCHAR(255) DEFAULT NULL,
    `payment_method` VARCHAR(255) DEFAULT NULL,
    `status` VARCHAR(255) DEFAULT NULL,
    `quantity` INT NOT NULL,
    `userid` INT NOT NULL,
    `sellerid` INT NOT NULL,
    `productid` INT DEFAULT NULL,
    `address` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
