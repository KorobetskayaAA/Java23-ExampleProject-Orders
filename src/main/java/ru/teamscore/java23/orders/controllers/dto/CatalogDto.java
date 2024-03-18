package ru.teamscore.java23.orders.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CatalogDto {
    private List<CatalogItemDto> items;
}
