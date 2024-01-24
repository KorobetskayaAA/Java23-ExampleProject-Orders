package ru.teamscore.java23.orders.model.enums;

import lombok.Getter;
import ru.teamscore.java23.orders.model.entities.Item;

import java.util.Comparator;

public enum CatalogSortOption {
    TITLE(Comparator.comparing(Item::getTitle)),
    PRICE(Comparator.comparing(Item::getPrice));

    @Getter
    Comparator<Item> comparator;

    CatalogSortOption(Comparator<Item> comparator) {
        this.comparator = comparator;
    }
}
