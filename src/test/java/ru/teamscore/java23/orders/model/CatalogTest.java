package ru.teamscore.java23.orders.model;

import org.junit.jupiter.api.Test;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.enums.CatalogSortOption;
import ru.teamscore.java23.orders.model.enums.ItemStatus;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogTest {
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

    @Test
    void addItem() {
        Catalog catalog = new Catalog();
        catalog.addItem(testItems[0]);
        assertEquals(1, catalog.getItemsCount());
        catalog.addItem(testItems[0]);
        assertEquals(1, catalog.getItemsCount());
        catalog.addItem(testItems[1]);
        assertEquals(2, catalog.getItemsCount());
    }

    @Test
    void getItem() {
        Catalog catalog = new Catalog();
        catalog.addItem(testItems[1]);
        catalog.addItem(testItems[0]);
        catalog.addItem(testItems[2]);
        testHasItem(testItems[0], catalog);
        testHasItem(testItems[1], catalog);
        testHasItem(testItems[2], catalog);
    }

    private void testHasItem(Item expectedItem, Catalog catalog) {
        var result = catalog.getItem(expectedItem.getBarcode());
        assertTrue(result.isPresent());
        assertEquals(expectedItem, result.get());
    }


    @Test
    void find() {
        Catalog catalog = new Catalog();
        for (Item item : testItems) {
            catalog.addItem(item);
        }
        var result = catalog.find("qwert");
        assertItems(new Item[]{}, catalog, result);
        result = catalog.find("46");
        assertItems(testItems, catalog, result);
        result = catalog.find(testItems[0].getBarcode().toString());
        assertItems(new Item[]{testItems[0]}, catalog, result);
    }

    @Test
    void getSorted() {
        Catalog catalog = new Catalog();
        for (Item item : testItems) {
            catalog.addItem(item);
        }
        var result = catalog.getSorted(CatalogSortOption.TITLE, false, 0, 10);
        assertItems(testItems, catalog, result);
        result = catalog.getSorted(CatalogSortOption.TITLE, true, 0, 10);
        assertItems(new Item[]{testItems[3], testItems[2], testItems[1], testItems[0]},
                catalog, result);
        result = catalog.getSorted(CatalogSortOption.TITLE, false, 0, 3);
        assertItems(new Item[]{testItems[0], testItems[1], testItems[2]},
                catalog, result);
        result = catalog.getSorted(CatalogSortOption.TITLE, false, 1, 3);
        assertItems(new Item[]{testItems[3]},
                catalog, result);
        result = catalog.getSorted(CatalogSortOption.PRICE, false, 0, 10);
        assertItems(new Item[]{testItems[1], testItems[2], testItems[0], testItems[3]},
                catalog, result);
    }

    private void assertItems(Item[] expectedItems, Catalog catalog, Collection<Item> result) {
        assertEquals(expectedItems.length, result.size(), "Wrong length");
        for (Item item : expectedItems) {
            assertTrue(result.contains(item), "Item missed " + item.getBarcode());
        }
    }
}