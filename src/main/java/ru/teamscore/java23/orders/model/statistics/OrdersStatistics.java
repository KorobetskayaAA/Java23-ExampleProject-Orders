package ru.teamscore.java23.orders.model.statistics;

import lombok.NonNull;
import lombok.Value;
import ru.teamscore.java23.orders.model.entities.Order;
import ru.teamscore.java23.orders.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Collection;

@Value
public class OrdersStatistics {
    private BigDecimal totalAmount;
    private int totalQuantity;
    private int totalItemsCount;
    private BigDecimal cancelAmount;
    private int cancelCount;

    public OrdersStatistics(@NonNull Collection<Order> orders) {

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        int totalItemsCount = 0;
        BigDecimal cancelAmount = BigDecimal.ZERO;
        int cancelCount = 0;

        for (Order order : orders) {
            BigDecimal orderAmount = order.getTotalAmount();
            totalAmount = totalAmount.add(orderAmount).setScale(2);
            totalQuantity += order.getTotalQuantity();
            totalItemsCount += order.getItemsCount();
            if (order.getStatus() == OrderStatus.CANCELED) {
                cancelAmount = cancelAmount.add(orderAmount);
                cancelCount++;
            }
        }
        this.totalAmount = totalAmount;
        this.totalQuantity = totalQuantity;
        this.totalItemsCount = totalItemsCount;
        this.cancelAmount = cancelAmount;
        this.cancelCount = cancelCount;
    }
}
