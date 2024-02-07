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
}