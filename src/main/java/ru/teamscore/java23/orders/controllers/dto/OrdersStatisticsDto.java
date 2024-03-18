package ru.teamscore.java23.orders.controllers.dto;

import lombok.Data;
import ru.teamscore.java23.orders.model.statistics.OrdersStatistics;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrdersStatisticsDto {
    private LocalDate month;
    private BigDecimal totalAmount;
    private long totalQuantity;
    private long totalItemsCount;
    private BigDecimal cancelAmount;
    private long cancelCount;

    public OrdersStatisticsDto(OrdersStatistics stats) {
        this.month = stats.getMonth();
        this.totalAmount = stats.getTotalAmount();
        this.totalQuantity = stats.getTotalQuantity();
        this.totalItemsCount = stats.getTotalItemsCount();
        this.cancelAmount = stats.getCancelAmount();
        this.cancelCount = stats.getCancelCount();
    }
}
