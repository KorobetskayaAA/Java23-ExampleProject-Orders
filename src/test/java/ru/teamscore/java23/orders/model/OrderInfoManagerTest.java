package ru.teamscore.java23.orders.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.entities.Order;
import ru.teamscore.java23.orders.model.enums.ItemStatus;
import ru.teamscore.java23.orders.model.enums.OrderStatus;
import ru.teamscore.java23.orders.model.statistics.OrdersStatistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class OrderInfoManagerTest {
    Random rnd = new Random(1000);
    int year = 2023;
    int fromMonth = 10;
    int toMonth = 12;
    BigDecimal expectedProcessingTotalAmount = BigDecimal.ZERO;
    int expectedProcessingTotalItems = 0;
    List<Order> orders = getOrders();

    // Тестовый OrdersManager
    OrdersManager ordersManager;

    // Для каждого теста - свой экземпляр
    @BeforeEach
    void setUp() {
        ordersManager = new OrdersManager();
        for (Order o : orders) {
            ordersManager.addOrder(o);
        }
    }

    @Test
    void getProcessingTotalAmount() {
        assertEquals(expectedProcessingTotalAmount,
                ordersManager.getInfo().getProcessingTotalAmount());
    }

    @Test
    void getProcessingOrderItems() {
        var items = ordersManager.getInfo().getProcessingOrderItems();
        assertEquals(expectedProcessingTotalItems, items.length);
    }

    @Test
    void getStatistics() {
        Map<LocalDate, OrdersStatistics> result = ordersManager.getInfo()
                .getStatistics(
                        LocalDate.of(year, fromMonth, 1),
                        LocalDate.of(year, toMonth, 1)
                );
        // Проверяем наличие результатов за каждый месяц.
        // Тестирование конкретных значений отдельно.
        assertEquals(toMonth - fromMonth + 1, result.size());
        for (var kvp : result.entrySet()) {
            assertEquals(year, kvp.getKey().getYear());
            int month = kvp.getKey().getMonthValue();
            assertTrue(month >= fromMonth && month <= toMonth);
            assertNotNull(kvp.getValue());
        }
    }

    private List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        for (int month = fromMonth; month <= toMonth; month++) {
            int ordersCount = rnd.nextInt(50, 100);
            for (int i = 1; i <= ordersCount; i++) {
                long orderId = month * 1000 + i;
                OrderStatus orderStatus = getRandomOrderStatus();
                Order order = createOrder(orderId, month, orderStatus);
                int itemsCount = rnd.nextInt(1, 10);
                addRandomOrderItems(itemsCount, order);
                if (orderStatus == OrderStatus.PROCESSING) {
                    expectedProcessingTotalItems += order.getItemsCount();
                    expectedProcessingTotalAmount = expectedProcessingTotalAmount.add(order.getTotalAmount());
                }
                orders.add(order);
            }
        }
        return orders;
    }

    private void addRandomOrderItems(int itemsCount, Order order) {
        for (int oi = 1; oi <= itemsCount; oi++) {
            addOrderItem(order, String.format("%05d123%05d", order.getId(), oi),
                    rnd.nextInt(1, 100),
                    rnd.nextInt(10, 10_000_000) / 100.0);
        }
    }

    private OrderStatus getRandomOrderStatus() {
        int orderStatusRnd = rnd.nextInt(100);
        OrderStatus orderStatus = orderStatusRnd < 50
                ? OrderStatus.CLOSED
                : orderStatusRnd < 80
                ? OrderStatus.PROCESSING
                : OrderStatus.CANCELED;
        return orderStatus;
    }

    private Order createOrder(long id, int month, OrderStatus status) {
        return Order.load(id,
                LocalDateTime.of(year, month, rnd.nextInt(1, 29), rnd.nextInt(24), rnd.nextInt(60)),
                "", status, new ArrayList<>());
    }

    private void addOrderItem(Order order, String barcode, int quantity, double price) {
        Item item = Item.load(new Barcode(barcode), "", ItemStatus.OPEN, new BigDecimal(price));
        order.addItem(item, quantity);
    }
}
