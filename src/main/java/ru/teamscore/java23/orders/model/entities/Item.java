package ru.teamscore.java23.orders.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.teamscore.java23.orders.model.enums.ItemStatus;

import java.math.BigDecimal;
import java.util.Objects;

@ToString
@AllArgsConstructor(staticName = "load")
@NoArgsConstructor
@Entity
@Table(name = "item", schema = "catalog")
@NamedQuery(name = "itemsCount", query = "select count(*) from Item")
@NamedQuery(name = "itemByBarcode", query = "from Item i where i.barcode = :barcode")
public class Item {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Setter
    @Column(name = "barcode", nullable = false, unique = true, length = 13)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Convert(converter = BarcodeConverter.class)
    private Barcode barcode;

    @Getter
    @Setter
    @Column(columnDefinition = "text")
    private String title;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.CLOSED;

    @Getter
    @Setter
    @Column(columnDefinition = "decimal(10,2)")
    private BigDecimal price;

    public boolean isAvailable() {
        return status.isAvailable();
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
        return Objects.equals(id, item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
