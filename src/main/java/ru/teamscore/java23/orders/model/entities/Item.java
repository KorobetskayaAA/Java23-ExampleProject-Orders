package ru.teamscore.java23.orders.model.entities;

import lombok.*;
import ru.teamscore.java23.orders.model.enums.ItemStatus;

import java.math.BigDecimal;
import java.util.Objects;

@ToString
@RequiredArgsConstructor()
@AllArgsConstructor(staticName = "load")
public class Item {
    @Getter
    private final Barcode barcode;

    @Getter
    @Setter
    private String title;

    private ItemStatus status = ItemStatus.CLOSED;

    @Getter
    @Setter
    private BigDecimal price;

    public boolean isAvaliable() {
        return status.isAvaliable();
    }

    public void open() {
        status = ItemStatus.OPEN;
    }

    public void close() {
        status = ItemStatus.CLOSED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(barcode, item.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode);
    }
}
