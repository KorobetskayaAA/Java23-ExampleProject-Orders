package ru.teamscore.java23.orders.controllers.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogItemDto {
    private String barcode;
    private String title;
    private String status;
    private BigDecimal price;
}
