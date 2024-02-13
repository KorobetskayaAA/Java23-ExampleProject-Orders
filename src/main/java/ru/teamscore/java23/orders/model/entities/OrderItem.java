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
    static class OrderItemPK {
        @Getter
        @Setter
        @ManyToOne
        @JoinColumn(name = "item_id")
        private Item item;

        @Getter
        @Setter
        @ManyToOne
        @JoinColumn(name = "order_id")
        private OrderWithItems order;
    }

    @EmbeddedId
    private OrderItemPK pk = new OrderItemPK();

    @Transient
    public Item getItem() {
        return pk.getItem();
    }

    @Transient
    public OrderWithItems getOrder() {
        return pk.getOrder();
    }

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

    public OrderItem(@NonNull Item item, @NonNull OrderWithItems order, int quantity) {
        pk.item = item;
        pk.order = order;
        this.price = item.getPrice();
        this.quantity = quantity;
    }
}
