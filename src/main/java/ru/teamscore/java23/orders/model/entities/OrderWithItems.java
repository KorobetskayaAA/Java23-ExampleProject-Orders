package ru.teamscore.java23.orders.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor(staticName = "load")
@Entity
@Table(name = "order", schema = "orders")
public class OrderWithItems {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0;

    @Getter
    @Embedded
    private CustomerOrder order = new CustomerOrder();

    @OneToMany(mappedBy = "pk.order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

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

    public Optional<OrderItem> getItem(@NonNull String barcode) {
        return getItem(new Barcode(barcode));
    }

    public Optional<OrderItem> getItem(@NonNull Item item) {
        return items.stream()
                .filter(oi -> oi.getItem().equals(item))
                .findFirst();
    }

    public Optional<OrderItem> getItem(@NonNull long itemId) {
        return items.stream()
                .filter(oi -> oi.getItem().getId() == itemId)
                .findFirst();
    }

    public OrderItem addItem(@NonNull Item item, int quantity) {
        // уже есть такой Item в списке
        var existingItem = getItem(item);
        if (existingItem.isPresent()) {
            existingItem.get().addQuantity(quantity);
            return existingItem.get();
        }
        // новый Item
        OrderItem oi = new OrderItem(item, this, quantity);
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

    public Optional<OrderItem> removeItem(String barcode) {
        return removeItem(new Barcode(barcode));
    }

    public Optional<OrderItem> removeItem(Item item) {
        var exisitngItem = getItem(item);
        if (exisitngItem.isPresent()) {
            items.remove(exisitngItem.get());
        }
        return exisitngItem;
    }
}
