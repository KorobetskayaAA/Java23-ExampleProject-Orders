package ru.teamscore.java23.orders.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.entities.Order;
import ru.teamscore.java23.orders.model.enums.OrderStatus;
import ru.teamscore.java23.orders.model.statistics.OrdersStatistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class OrdersManager {
    private final List<Order> orders = new ArrayList<>();

    @Getter()
    private final OrderInfoManager info = new OrderInfoManager();

    public int getOrdersCount() {
        return orders.size();
    }

    public Order[] getOrdersAll() {
        return orders.stream().toArray(Order[]::new);
    }

    public Optional<Order> getOrder(long id) {
        return orders.stream()
                .filter(o -> o.getId() == id)
                .findFirst();
    }

    public void addOrder(Order order) {
        if (getOrder(order.getId()).isEmpty()) {
            orders.add(order);
        }
    }

    public class OrderInfoManager {
        public BigDecimal getProcessingTotalAmount() {
            return orders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.PROCESSING)
                    .map(o -> o.getTotalAmount())
                    .reduce(BigDecimal.ZERO, (total, amount) -> total.add(amount))
                    .setScale(2);
        }

        public Item[] getProcessingOrderItems() {
            return orders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.PROCESSING)
                    .flatMap(o -> o.getItemsAll().stream().map(oi -> oi.getItem()))
                    .distinct()
                    .toArray(Item[]::new);
        }

        public Map<LocalDate, OrdersStatistics> getStatistics(LocalDate from, LocalDate to) {
            Map<LocalDate, List<Order>> ordersByMonth = orders.stream()
                    .filter(o -> {
                        var created = o.getCreated();
                        return created.isAfter(from.atStartOfDay()) &&
                                created.isBefore(to.plusDays(1).atStartOfDay());
                    })
                    .collect(Collectors.groupingBy(o -> o.getCreated().withDayOfMonth(1).toLocalDate()));
            return ordersByMonth.entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(),
                            new OrdersStatistics(entry.getValue())))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
    }
}
