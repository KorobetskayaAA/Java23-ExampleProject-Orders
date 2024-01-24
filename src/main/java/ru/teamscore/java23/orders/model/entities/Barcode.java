package ru.teamscore.java23.orders.model.entities;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Barcode {
    private final static String BARCODE_PATTERN = "\\d{13}";
    @EqualsAndHashCode.Include
    private final String barcode;

    public Barcode(@NonNull String barcode) {
        barcode = barcode.trim();
        if (!isValid(barcode)) {
            throw new IllegalArgumentException("Переданная строка " +
                    barcode + " не является корректным штрих-кодом");
        }
        this.barcode = barcode;
    }

    public static boolean isValid(@NonNull String barcode) {
        return barcode
                .trim()
                .matches(BARCODE_PATTERN);
    }

    @Override
    public String toString() {
        return barcode;
    }
}
