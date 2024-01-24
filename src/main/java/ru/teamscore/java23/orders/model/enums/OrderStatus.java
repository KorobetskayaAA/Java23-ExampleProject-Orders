package ru.teamscore.java23.orders.model.enums;

public enum OrderStatus {
    PROCESSING("в работе"),
    CLOSED("выполнен"),
    CANCELED("отменён");

    private final String title;

    OrderStatus(String title) {
        this.title = title;
    }
}
