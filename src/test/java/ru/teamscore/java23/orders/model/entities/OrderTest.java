package ru.teamscore.java23.orders.model.entities;

import org.junit.jupiter.api.Test;
import ru.teamscore.java23.orders.model.enums.ItemStatus;
import ru.teamscore.java23.orders.model.enums.OrderStatus;
import ru.teamscore.java23.orders.model.exceptions.OrderSetStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    Item[] testItems = new Item[]{
            Item.load(
                    new Barcode("4600000000001"),
                    "Товар 1",
                    ItemStatus.OPEN,
                    new BigDecimal(99.99)
            ),
            Item.load(
                    new Barcode("4600000000002"),
                    "Товар 2",
                    ItemStatus.OPEN,
                    new BigDecimal(2.00)
            ),
            Item.load(
                    new Barcode("4600000000003"),
                    "Товар 3",
                    ItemStatus.CLOSED,
                    new BigDecimal(50.05)
            ),
            Item.load(
                    new Barcode("4600000000004"),
                    "Товар 4",
                    ItemStatus.OPEN,
                    new BigDecimal(102.50)
            )
    };
    Order.OrderItem[] testOrderItems = new Order.OrderItem[]{
            Order.OrderItem.load(testItems[0], 1, testItems[0].getPrice()),
            Order.OrderItem.load(testItems[1], 100, BigDecimal.TEN),
            Order.OrderItem.load(testItems[2], 9, testItems[2].getPrice())
    };

    private Order getOrderWithItems() {
        return Order.load(1,
                LocalDateTime.now(),
                "Тестовый Покупатель",
                OrderStatus.PROCESSING,
                new ArrayList<>(Arrays.stream(testOrderItems).toList())
        );
    }

    @Test
    void close() {
        Order order = getOrderWithItems();
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        order.close();
        assertEquals(OrderStatus.CLOSED, order.getStatus());
        order.close();
        assertEquals(OrderStatus.CLOSED, order.getStatus());
        OrderSetStatusException ex = assertThrows(OrderSetStatusException.class, () -> order.cancel());
        assertEquals(OrderStatus.CLOSED, ex.getOldStatus());
        assertEquals(OrderStatus.CANCELED, ex.getNewStatus());
    }

    @Test
    void cancel() {
        Order order = getOrderWithItems();
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        order.cancel();
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        order.cancel();
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        OrderSetStatusException ex = assertThrows(OrderSetStatusException.class, () -> order.close());
        assertEquals(OrderStatus.CANCELED, ex.getOldStatus());
        assertEquals(OrderStatus.CLOSED, ex.getNewStatus());
    }

    @Test
    void getItemsAll() {
        Order order = getOrderWithItems();
        var itemsAll = order.getItemsAll();
        assertEquals(testOrderItems.length, itemsAll.size());
        for (var item : itemsAll) {
            assertTrue(Arrays.stream(testOrderItems).anyMatch((oi) ->
                    oi.getItem() == item.getItem() &&
                            oi.getQuantity() == item.getQuantity() &&
                            oi.getPrice().equals(item.getPrice())
            ));
        }
    }

    @Test
    void getOrderItemAmount() {
        assertEquals("99.99", testOrderItems[0].getAmount().toPlainString());
        assertEquals("1000.00", testOrderItems[1].getAmount().toPlainString());
        assertEquals("450.45", testOrderItems[2].getAmount().toPlainString());
    }

    @Test
    void getTotalAmount() {
        Order order = getOrderWithItems();
        assertEquals("1550.44", order.getTotalAmount().toPlainString());
    }

    @Test
    void getItemsCount() {
        Order order = getOrderWithItems();
        assertEquals(testOrderItems.length, order.getItemsCount());
    }

    @Test
    void getItem() {
        Order order = getOrderWithItems();
        Barcode barcode = testOrderItems[1].getItem().getBarcode();
        var orderItem = order.getItem(barcode);
        assertTrue(orderItem.isPresent());
        assertEquals(testOrderItems[1], orderItem.get());
    }

    @Test
    void addItem() {
        Order order = getOrderWithItems();
        Item itemToAdd = testItems[testItems.length - 1];
        Order.OrderItem addedItem = order.addItem(itemToAdd, 2);
        assertEquals(testOrderItems.length + 1, order.getItemsAll().size());
        assertEquals(2, addedItem.getQuantity());
        addedItem = order.addItem(itemToAdd);
        assertEquals(testOrderItems.length + 1, order.getItemsAll().size());
        assertEquals(3, addedItem.getQuantity());
    }

    @Test
    void removeItem() {
        Order order = getOrderWithItems();
        Item itemToRemove = testItems[0];
        var removedItem = order.removeItem(itemToRemove.getBarcode());
        assertEquals(testOrderItems.length - 1, order.getItemsAll().size());
        assertTrue(removedItem.isPresent());
        assertEquals(itemToRemove, removedItem.get().getItem());
    }
}