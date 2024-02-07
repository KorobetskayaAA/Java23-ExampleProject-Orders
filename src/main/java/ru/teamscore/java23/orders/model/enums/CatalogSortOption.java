package ru.teamscore.java23.orders.model.enums;

import lombok.Getter;

public enum CatalogSortOption {
    TITLE("title"),
    PRICE("price");

    @Getter
    String columnName;

    CatalogSortOption(String columnName) {
        this.columnName = columnName;
    }
}
