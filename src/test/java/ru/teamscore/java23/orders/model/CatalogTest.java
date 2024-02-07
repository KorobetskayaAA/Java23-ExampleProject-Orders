package ru.teamscore.java23.orders.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.enums.CatalogSortOption;
import ru.teamscore.java23.orders.model.enums.ItemStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    public static void setup() throws IOException {
        entityManagerFactory = new Configuration()
                .configure("hibernate-postgres.cfg.xml")
                .addAnnotatedClass(Item.class)
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
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    public void closeSession() throws IOException {
        if (entityManager != null) {
            entityManager.close();
        }
        SqlScripts.runFromFile(entityManagerFactory, "clearTestItems.sql");
    }

    @ParameterizedTest
    @ValueSource(strings = {"4601234500001", "4601234500010", "4601234500030"})
    void getItemExists(String barcode) {
        Catalog catalog = new Catalog(entityManager);
        var result = catalog.getItem(barcode);
        assertTrue(result.isPresent());
        assertEquals(barcode, result.get().getBarcode().toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 30})
    void getItemExists(long id) {
        Catalog catalog = new Catalog(entityManager);
        var result = catalog.getItem(id);
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"9901234500001", "0000000000000", "1234567890123"})
    void getItemNotExists(String barcode) {
        Catalog catalog = new Catalog(entityManager);
        var result = catalog.getItem(barcode);
        assertTrue(result.isEmpty());
    }

    @Test
    void addItem() {
        Catalog catalog = new Catalog(entityManager);
        Item[] itemsToAdd = new Item[]{
                Item.load(
                        0,
                        new Barcode("4601000000001"),
                        "Товар 1",
                        ItemStatus.OPEN,
                        new BigDecimal(99.99)
                ),
                Item.load(
                        0,
                        new Barcode("4600100000002"),
                        "Товар 2",
                        ItemStatus.OPEN,
                        new BigDecimal(2.00)
                )
        };
        long startCount = catalog.getItemsCount();
        assertTrue(catalog.getItem(itemsToAdd[0].getBarcode()).isEmpty(), "Item not exists before add");
        assertTrue(catalog.getItem(itemsToAdd[1].getBarcode()).isEmpty(), "Item not exists before add");
        catalog.addItem(itemsToAdd[0]);
        assertEquals(startCount + 1, catalog.getItemsCount(), "Item added");
        assertTrue(catalog.getItem(itemsToAdd[0].getBarcode()).isPresent());
        catalog.addItem(itemsToAdd[0]);
        assertEquals(startCount + 1, catalog.getItemsCount(), "Item not added, already exists");
        catalog.addItem(itemsToAdd[1]);
        assertEquals(startCount + 2, catalog.getItemsCount(), "Item added");
    }



    @Test
    void find() {
        Catalog catalog = new Catalog(entityManager);
        var result = catalog.find("qwert");
        assertEquals(0, result.size());
        result = catalog.find("46");
        assertEquals(30, result.size());
        result = catalog.find("4601234500007");
        assertEquals(1, result.size());
    }

    @Test
    void getSorted() {
        Catalog catalog = new Catalog(entityManager);
        testSorted(catalog, CatalogSortOption.TITLE, false, 0, 10,
                (i1, i2) -> i1.getTitle().compareTo(i2.getTitle()) >= 0);
        testSorted(catalog, CatalogSortOption.TITLE, true, 0, 10,
                (i1, i2) -> i1.getTitle().compareTo(i2.getTitle()) <= 0);
        testSorted(catalog, CatalogSortOption.PRICE, false, 1, 3,
                (i1, i2) -> i1.getPrice().compareTo(i2.getPrice()) >= 0);
        testSorted(catalog, CatalogSortOption.PRICE, true, 5, 5,
                (i1, i2) -> i1.getPrice().compareTo(i2.getPrice()) <= 0);
    }

    private void testSorted(Catalog catalog,
                            CatalogSortOption option, boolean desc, int page, int pageSize,
                            BiPredicate<Item, Item> compare) {
        var result = catalog.getSorted(option, desc, page, pageSize);
        assertEquals(pageSize, result.length);
        for (int i = 1; i < result.length; i++) {
            assertTrue(compare.test(result[i], result[i - 1]));
        }
    }
}