package ru.teamscore.java23.orders.model.enums;

import lombok.Getter;

public enum OrderStatus {
    PROCESSING("в работе", true),
    CLOSED("выполнен", false),
    CANCELED("отменён", false);

    @Getter
    private final String title;
    @Getter
    private final boolean editable;

    OrderStatus(String title, boolean editable) {
        this.title = title;
        this.editable = editable;
    }
}
