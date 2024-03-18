package ru.teamscore.java23.orders.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.entities.OrderItem;
import ru.teamscore.java23.orders.model.entities.OrderWithItems;
import ru.teamscore.java23.orders.model.statistics.OrdersStatistics;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderInfoManagerTest {
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
                .addAnnotatedClass(OrdersStatistics.class)
                .buildSessionFactory();
        SqlScripts.runFromFile(entityManagerFactory, "createSchema.sql");
        SqlScripts.runFromFile(entityManagerFactory, "viewOrderStatistics.sql");
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
    void getProcessingTotalAmount() {
        assertEquals(new BigDecimal("2701059.14"),
                ordersManager.getInfo().getProcessingTotalAmount());
    }

    @Test
    void getProcessingOrderItems() {
        var items = ordersManager.getInfo().getProcessingOrderItems();
        // all items are unique
        var uniqueItems = new HashSet<OrderItem>(items.size());
        uniqueItems.addAll(items);
        assertEquals(uniqueItems.size(), items.size());
    }

    @Test
    void getStatistics() {
        int year = 2023;
        int month = 1;
        var results = ordersManager.getInfo()
                .getStatistics(
                        LocalDate.of(year, month, 1),
                        LocalDate.of(year, month, 1)
                );
        // Проверяем наличие результатов за каждый месяц.
        // Тестирование конкретных значений отдельно.
        assertEquals(1, results.size());
        var result = results.get(0);
        assertEquals(year, result.getMonth().getYear());
        assertEquals(month, result.getMonth().getMonthValue());
        assertEquals(new BigDecimal("8703032.16"), result.getTotalAmount());
        assertEquals(1186, result.getTotalQuantity());
        assertEquals(26, result.getTotalItemsCount());
        assertEquals(new BigDecimal("183274.49"), result.getCancelAmount());
        assertEquals(24, result.getCancelCount());
    }
}
