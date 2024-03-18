package ru.teamscore.java23.orders.model.statistics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Immutable
@Table(name = "order_statistics", schema = "orders")
public class OrdersStatistics {
    @Id
    private LocalDate month;
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    @Column(name = "total_quantity")
    private long totalQuantity;
    @Column(name = "total_items_count")
    private long totalItemsCount;
    @Column(name = "cancel_amount")
    private BigDecimal cancelAmount;
    @Column(name = "cancel_quantity")
    private long cancelCount;
}
