package ru.teamscore.java23.orders.controllers.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.teamscore.java23.orders.model.entities.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderCreateDto {
    private String customerName;

    private final List<OrderItemCreateDto> items = new ArrayList<>();

    private OrderItemCreateDto newItem = new OrderItemCreateDto();

    public void addItem() {
        var existingItem = items.stream()
                .filter(oi -> oi.getBarcode().equals(newItem.getBarcode()))
                .findFirst();
        if (existingItem.isPresent()) {
            existingItem.get().addQuantity(newItem.getQuantity());
        } else {
            items.add(newItem);
        }
        newItem = new OrderItemCreateDto();
    }

    public void removeItem(String barcode) {
        var existingItem = items.stream()
                .filter(oi -> oi.getBarcode().equals(barcode))
                .findFirst();
        if (existingItem.isPresent()) {
            items.remove(existingItem.get());
        }
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(oi -> oi.item.getPrice().multiply(BigDecimal.valueOf(oi.quantity)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemCreateDto {
        private String barcode;
        @Min(1)
        private int quantity = 1;
        private Item item;

        public void addQuantity(int quantity) {
            this.quantity += quantity;
        }
    }
}
