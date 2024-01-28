package ru.teamscore.java23.orders.model.entities;

import lombok.*;
import ru.teamscore.java23.orders.model.enums.OrderStatus;
import ru.teamscore.java23.orders.model.exceptions.OrderSetStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@AllArgsConstructor(staticName = "load")
public class Order {
    @Getter
    private final long id;

    @Getter
    private final LocalDateTime created;

    @Getter
    @Setter
    private String customerName;

    @Getter
    private OrderStatus status = OrderStatus.PROCESSING;

    private final List<OrderItem> items;

    public Order(int id) {
        this.id = id;
        created = LocalDateTime.now();
        items = new ArrayList<>();
    }

    public void close() {
        if (status == OrderStatus.CANCELED) {
            throw new OrderSetStatusException("Нельзя завершить отмененный заказ", status, OrderStatus.CLOSED);
        }
        status = OrderStatus.CLOSED;
    }

    public void cancel() {
        if (status == OrderStatus.CLOSED) {
            throw new OrderSetStatusException("Нельзя отменить закрытый заказ", status, OrderStatus.CANCELED);
        }
        status = OrderStatus.CANCELED;
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(i -> i.getAmount())
                .reduce(BigDecimal.ZERO, (sum, amount) -> sum.add(amount))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int getTotalQuantity() {
        return items.stream()
                .map(oi -> oi.getQuantity())
                .reduce(0, (sum, q) -> sum + q);
    }

    public Collection<OrderItem> getItemsAll() {
        return items.stream().toList();
    }

    public int getItemsCount() {
        return items.size();
    }

    public Optional<OrderItem> getItem(@NonNull Barcode barcode) {
        return items.stream()
                .filter(oi -> oi.getItem().getBarcode().equals(barcode))
                .findFirst();
    }

    public Optional<OrderItem> getItem(@NonNull Item item) {
        return items.stream()
                .filter(oi -> oi.getItem().equals(item))
                .findFirst();
    }

    public OrderItem addItem(@NonNull Item item, int quantity) {
        // уже есть такой Item в списке
        var existingItem = getItem(item.getBarcode());
        if (existingItem.isPresent()) {
            existingItem.get().addQuantity(quantity);
            return existingItem.get();
        }
        // новый Item
        OrderItem oi = new OrderItem(item, quantity);
        items.add(oi);
        return oi;
    }

    public OrderItem addItem(@NonNull Item item) {
        return addItem(item, 1);
    }

    public Optional<OrderItem> removeItem(Barcode barcode) {
        var exisitngItem = getItem(barcode);
        if (exisitngItem.isPresent()) {
            items.remove(exisitngItem.get());
        }
        return exisitngItem;
    }

    public Optional<OrderItem> removeItem(Item item) {
        var exisitngItem = getItem(item);
        if (exisitngItem.isPresent()) {
            items.remove(exisitngItem.get());
        }
        return exisitngItem;
    }

    @AllArgsConstructor(staticName = "load")
    public static class OrderItem {
        @Getter
        private final Item item;
        @Getter
        @Setter
        private int quantity = 1;
        @Getter
        private BigDecimal price;

        public BigDecimal getAmount() {
            return price.multiply(new BigDecimal(quantity))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        public int addQuantity(int quantity) {
            this.quantity += quantity;
            return this.quantity;
        }

        public OrderItem(@NonNull Item item, int quantity) {
            this.item = item;
            this.price = item.getPrice();
            this.quantity = quantity;
        }
    }
}
