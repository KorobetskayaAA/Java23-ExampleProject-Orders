package ru.teamscore.java23.orders.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.teamscore.java23.orders.model.entities.Order;
import ru.teamscore.java23.orders.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersManagerTest {
    // Набор заказов для теста
    List<Order> orders = new ArrayList<>();

    {
        orders.add(Order.load(1,
                LocalDateTime.now(),
                "Покупатель 1",
                OrderStatus.PROCESSING,
                new ArrayList<>()
        ));
        orders.add(Order.load(2,
                LocalDateTime.now(),
                "Покупатель 2",
                OrderStatus.PROCESSING,
                new ArrayList<>()
        ));
        orders.add(Order.load(3,
                LocalDateTime.now(),
                "Покупатель 3",
                OrderStatus.PROCESSING,
                new ArrayList<>()
        ));
    }

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
    void getOrdersCount() {
        assertEquals(orders.size(), ordersManager.getOrdersCount());
    }

    @Test
    void getOrdersAll() {
        var allOrders = ordersManager.getOrdersAll();
        assertEquals(orders.size(), allOrders.length);
        for (Order order : orders) {
            assertTrue(Arrays.stream(allOrders)
                    .anyMatch(o -> o.equals(order)));
        }
    }

    @Test
    void getOrder() {
        for (Order order : orders) {
            assertEquals(order, ordersManager.getOrder(order.getId()).get());
        }
    }

}