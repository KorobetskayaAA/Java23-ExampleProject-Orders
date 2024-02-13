package ru.teamscore.java23.orders.model.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

    @Test
    void isAvaliable() {
        Item item = new Item();
        item.setBarcode(new Barcode("1234567890123"));
        assertFalse(item.isAvailable());
        item.open();
        assertTrue(item.isAvailable());
        item.close();
        assertFalse(item.isAvailable());
        item.open();
        assertTrue(item.isAvailable());
    }
}