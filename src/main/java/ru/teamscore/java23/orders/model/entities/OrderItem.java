package ru.teamscore.java23.orders.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@Entity
@Table(name = "order_item", schema = "orders")
public class OrderItem {
    @Embeddable
    private class OrderItemPK {
        @Column(name = "order_id", nullable = false)
        private long orderId;
        @Column(name = "item_id", nullable = false)
        private long itemId;
    }

    @EmbeddedId
    private OrderItemPK pk = new OrderItemPK();

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderWithItems order;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "decimal(10,2)")
    private BigDecimal price;

    @Getter
    @Setter
    @Column(nullable = false)
    private int quantity = 1;

    public BigDecimal getAmount() {
        return price.multiply(new BigDecimal(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int addQuantity(int quantity) {
        this.quantity += quantity;
        return this.quantity;
    }

    public OrderItem(@NonNull Item item, int quantity) {
        pk.itemId = item.getId();
        this.item = item;
        this.price = item.getPrice();
        this.quantity = quantity;
    }
}
