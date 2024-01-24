package ru.teamscore.java23.orders.model.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BarcodeTest {

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123", "  1234567890123\t", "0000000000000"})
    void isValidTrue(String value) {
        assertTrue(Barcode.isValid(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "12345678901234", "12345 67890123", "", "abcdeabcde123"})
    void isValidFalse(String value) {
        assertFalse(Barcode.isValid(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123", "  1234567890123\t", "0000000000000"})
    void testToString(String value) {
        Barcode barcode = new Barcode(value);
        assertEquals(value.trim(), barcode.toString());
    }

    @Test
    void testEqualsTrue() {
        String value = "1234567890123";
        Barcode barcode1 = new Barcode(value);
        Barcode barcode2 = new Barcode(value);
        assertTrue(barcode1.equals(barcode2));
        assertTrue(barcode2.equals(barcode1));
    }

    @Test
    void testEqualsFalse() {
        String value1 = "1234567890123";
        String value2 = "1234567890987";
        Barcode barcode1 = new Barcode(value1);
        Barcode barcode2 = new Barcode(value2);
        assertFalse(barcode1.equals(barcode2));
        assertFalse(barcode2.equals(barcode1));
        assertFalse(barcode1.equals(value1));
    }
}