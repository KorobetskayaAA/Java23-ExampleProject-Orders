package ru.teamscore.java23.orders.model.enums;

import lombok.Getter;

public enum ItemStatus {
    OPEN(true),
    CLOSED(false);

    @Getter
    private boolean available;

    ItemStatus(boolean available) {
        this.available = available;
    }
}
