package ru.teamscore.java23.orders.model.statistics;

import org.junit.jupiter.api.Test;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.entities.Order;
import ru.teamscore.java23.orders.model.enums.ItemStatus;
import ru.teamscore.java23.orders.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrdersStatisticsTest {
    @Test
    void testConstructor() {
        List<Order> orders = getOrders();
        OrdersStatistics result = new OrdersStatistics(orders);
        assertEquals("37387.92", result.getTotalAmount().toPlainString());
        assertEquals(117, result.getTotalQuantity());
        assertEquals(6, result.getTotalItemsCount());
        assertEquals("210.00", result.getCancelAmount().toPlainString());
        assertEquals(1, result.getCancelCount());
    }

    private List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        Order order = createOrder(1, OrderStatus.CANCELED);
        addOrderItem(order, "1234512345001", 1, 10.0);
        addOrderItem(order, "1234512345002", 100, 2.0);
        orders.add(order);
        order = createOrder(2, OrderStatus.PROCESSING);
        addOrderItem(order, "1234512345003", 10, 9.99);
        addOrderItem(order, "1234512345004", 1, 0.01);
        addOrderItem(order, "1234512345005", 2, 20.5);
        orders.add(order);
        order = createOrder(2, OrderStatus.CLOSED);
        addOrderItem(order, "1234512345006", 3, 12345.67);
        orders.add(order);
        return orders;
    }

    private Order createOrder(int id, OrderStatus status) {
        return Order.load(id, LocalDateTime.now(), "", status, new ArrayList<>());
    }

    private void addOrderItem(Order order, String barcode, int quantity, double price) {
        Item item = Item.load(new Barcode(barcode), "", ItemStatus.OPEN, new BigDecimal(price));
        order.addItem(item, quantity);
    }
}