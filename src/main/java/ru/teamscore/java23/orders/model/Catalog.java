package ru.teamscore.java23.orders.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.enums.CatalogSortOption;

import java.util.*;

@RequiredArgsConstructor
public class Catalog {
    private final List<Item> items = new ArrayList<>();

    public int getItemsCount() {
        return items.size();
    }

    public Optional<Item> getItem(@NonNull Barcode barcode) {
        return items.stream()
                .filter(i -> i.getBarcode().equals(barcode))
                .findFirst();
    }

    public void addItem(@NonNull Item item) {
        if (getItem(item.getBarcode()).isEmpty()) {
            items.add(item);
        }
    }

    public Collection<Item> find(String search) {
        String pattern = ".*" + search + ".*";
        return items.stream()
                .filter(i -> i.getTitle().matches(pattern) ||
                        i.getBarcode().toString().matches(pattern))
                .toList();
    }

    public Collection<Item> getSorted(CatalogSortOption option, boolean desc, int page, int pageSize) {
        Comparator comparator = option.getComparator();
        if (desc) {
            comparator = comparator.reversed();
        }
        return items.stream()
                .sorted(comparator)
                .skip(page * pageSize)
                .limit(pageSize)
                .toList();
    }
}
