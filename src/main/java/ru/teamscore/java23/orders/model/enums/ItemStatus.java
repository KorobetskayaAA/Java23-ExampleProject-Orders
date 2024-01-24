package ru.teamscore.java23.orders.model.enums;

import lombok.Getter;

public enum ItemStatus {
    OPEN(true),
    CLOSED(false);

    @Getter
    private boolean avaliable;

    ItemStatus(boolean avaliable) {
        this.avaliable = avaliable;
    }
}
