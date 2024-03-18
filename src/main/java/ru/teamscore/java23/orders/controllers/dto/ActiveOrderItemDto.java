package ru.teamscore.java23.orders.controllers.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActiveOrderItemDto {
    private String barcode;
    private String title;
    private BigDecimal price;
    private int quantity;
}
