package ru.teamscore.java23.orders.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.entities.OrderItem;
import ru.teamscore.java23.orders.model.entities.OrderWithItems;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersManagerTest {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private OrdersManager ordersManager;

    @BeforeAll
    public static void setup() throws IOException {
        entityManagerFactory = new Configuration()
                .configure("hibernate-postgres.cfg.xml")
                .addAnnotatedClass(Item.class)
                .addAnnotatedClass(OrderWithItems.class)
                .addAnnotatedClass(OrderItem.class)
                .buildSessionFactory();
        SqlScripts.runFromFile(entityManagerFactory, "createSchema.sql");
    }

    @AfterAll
    public static void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
    @BeforeEach
    public void openSession() throws IOException {
        SqlScripts.runFromFile(entityManagerFactory, "insertTestItems.sql");
        SqlScripts.runFromFile(entityManagerFactory, "insertTestOrders.sql");
        entityManager = entityManagerFactory.createEntityManager();
        ordersManager = new OrdersManager(entityManager);
    }

    @AfterEach
    public void closeSession() throws IOException {
        if (entityManager != null) {
            entityManager.close();
        }
        SqlScripts.runFromFile(entityManagerFactory, "clearTestOrders.sql");
        SqlScripts.runFromFile(entityManagerFactory, "clearTestItems.sql");
    }



    @Test
    void getOrdersCount() {
        assertEquals(50, ordersManager.getOrdersCount());
    }

    @Test
    void getOrdersAll() {
        var allOrders = ordersManager.getOrdersAll();
        assertEquals(50, allOrders.length);
        for (int i = 1; i <= allOrders.length; i++) {
            int finalId = i;
            assertTrue(Arrays.stream(allOrders).anyMatch(o -> o.getId() == finalId),
                    finalId + " id is missing");
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 5, 15, 28, 50})
    void getOrder(long id) {
        var order = ordersManager.getOrder(id);
        assertTrue(order.isPresent());
        assertEquals(id, order.get().getId());
    }

    @Test
    void addOrder() {
        // prepare item to insert into order
        long itemId = 2;
        Catalog catalog = new Catalog(entityManager);
        var itemToAddOptional = catalog.getItem(itemId);
        assertTrue(itemToAddOptional.isPresent(), "Item to insert not found");
        var itemToAdd = itemToAddOptional.get();
        // create order
        OrderWithItems orderToAdd = new OrderWithItems();
        orderToAdd.getOrder().setCustomerName("TEST TEST");
        orderToAdd.addItem(itemToAdd, 1);
        // save order
        long count = ordersManager.getOrdersCount();
        ordersManager.addOrder(orderToAdd);
        assertEquals(count + 1, orderToAdd.getId());
        // assert
        var addedOrder = ordersManager.getOrder(orderToAdd.getId());
        assertTrue(addedOrder.isPresent());
        assertEquals(orderToAdd.getOrder().getCustomerName(), addedOrder.get().getOrder().getCustomerName());
        assertEquals(orderToAdd.getOrder().getStatus(), addedOrder.get().getOrder().getStatus());
        var addedItem = addedOrder.get().getItem(itemToAdd.getBarcode());
        assertTrue(addedItem.isPresent());
        assertEquals(itemToAdd.getPrice(), addedItem.get().getPrice());
        assertEquals(1, addedItem.get().getQuantity());
    }

    @Test
    void updateOrder() {
        long orderId = 1;
        long itemId = 23;
        int newQuantity = 10000;
        String newCustomerName = "TEST TEST";
        // get order
        var existingOrder = ordersManager.getOrder(orderId);
        assertTrue(existingOrder.isPresent(), "Order with id = " + orderId + " should exist");
        OrderWithItems order = existingOrder.get();
        // change item in order
        var orderItemToEdit = order.getItem(itemId);
        assertTrue(orderItemToEdit.isPresent(), "Item with id = " + itemId + " should exist in order");
        orderItemToEdit.get().setQuantity(newQuantity);
        // change order
        order.getOrder().setCustomerName(newCustomerName);
        // save changes
        ordersManager.updateOrder(order);
        // reload from DB and assert changes
        var orderAfterUpdate = ordersManager.getOrder(orderId);
        assertTrue(orderAfterUpdate.isPresent(), "Order with id = " + orderId + " disappeared after update");
        var orderItemAfterUpdate = order.getItem(itemId);
        assertTrue(orderItemAfterUpdate.isPresent(), "Item with id = " + itemId + " disappeared after update");
        assertEquals(newCustomerName, orderAfterUpdate.get().getOrder().getCustomerName());
        assertEquals(newQuantity, orderItemAfterUpdate.get().getQuantity());
    }
}