package ru.teamscore.java23.orders.model.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BarcodeConverter implements AttributeConverter<Barcode, String> {

    @Override
    public String convertToDatabaseColumn(Barcode attribute) {
        return attribute.toString();
    }

    @Override
    public Barcode convertToEntityAttribute(String dbData) {
        return new Barcode(dbData);
    }
}