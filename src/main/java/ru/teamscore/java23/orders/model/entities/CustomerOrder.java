package ru.teamscore.java23.orders.model.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.teamscore.java23.orders.model.enums.OrderStatus;
import ru.teamscore.java23.orders.model.exceptions.OrderSetStatusException;

import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.MODULE)
@Embeddable
public class CustomerOrder {
    @Getter
    @Basic
    private final LocalDateTime created;

    @Getter
    @Setter
    @Column(name = "customer_name")
    private String customerName;

    @Getter
    @Enumerated
    @Column(nullable = false, length = 15)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private OrderStatus status = OrderStatus.PROCESSING;

    public CustomerOrder() {
        created = LocalDateTime.now();
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
}
