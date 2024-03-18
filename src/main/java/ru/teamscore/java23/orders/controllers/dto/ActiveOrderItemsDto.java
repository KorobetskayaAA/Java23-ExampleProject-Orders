package ru.teamscore.java23.orders.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ActiveOrderItemsDto {
    private List<ActiveOrderItemDto> items;
    private BigDecimal totalAmount;
}
