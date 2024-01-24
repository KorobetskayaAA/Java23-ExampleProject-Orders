package ru.teamscore.java23.orders.model.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

    @Test
    void isAvaliable() {
        Item item = new Item(new Barcode("1234567890123"));
        assertFalse(item.isAvaliable());
        item.open();
        assertTrue(item.isAvaliable());
        item.close();
        assertFalse(item.isAvaliable());
        item.open();
        assertTrue(item.isAvaliable());
    }
}