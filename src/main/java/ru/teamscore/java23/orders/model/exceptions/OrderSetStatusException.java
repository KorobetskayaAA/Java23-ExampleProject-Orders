package ru.teamscore.java23.orders.model.exceptions;

import lombok.Getter;
import ru.teamscore.java23.orders.model.enums.OrderStatus;

public class OrderSetStatusException extends RuntimeException {
    @Getter
    private OrderStatus oldStatus;
    @Getter
    private OrderStatus newStatus;

    public OrderSetStatusException(String message, OrderStatus oldStatus, OrderStatus newStatus) {
        super(message);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
