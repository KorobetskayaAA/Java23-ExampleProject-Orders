package ru.teamscore.java23.orders.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.enums.CatalogSortOption;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class Catalog {
    private final EntityManager entityManager;

    public long getItemsCount() {
        return entityManager
                .createNamedQuery("itemsCount", Long.class)
                .getSingleResult();
    }

    public Optional<Item> getItem(@NonNull Barcode barcode) {
        return getItem(barcode.toString());
    }

    public Optional<Item> getItem(@NonNull String barcode) {
        try {
            return Optional.of(entityManager
                    .createNamedQuery("itemByBarcode", Item.class)
                    .setParameter("barcode", barcode)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Item> getItem(long id) {
        return Optional.of(entityManager.find(Item.class, id));
    }

    public Optional<Item> getItem(@NonNull Item item) {
        if (entityManager.contains(item)) {
            return Optional.of(item);
        }
        return getItem(item.getId());
    }

    public void addItem(@NonNull Item item) {
        entityManager.getTransaction().begin();
        entityManager.persist(item);
        entityManager.getTransaction().commit();
    }

    public Collection<Item> find(String search) {
        String pattern = "%" + search + "%";
        return entityManager
                .createQuery("from Item where title ilike :pattern or barcode ilike :pattern", Item.class)
                .setParameter("pattern", pattern)
                .getResultList();
    }

    public Item[] getSorted(CatalogSortOption option, boolean desc, int page, int pageSize) {
        var query = entityManager.getCriteriaBuilder()
                .createQuery(Item.class);
        Root<Item> root = query.from(Item.class);
        var sortBy = root.get(option.getColumnName());
        var order = desc
                ? entityManager.getCriteriaBuilder().desc(sortBy)
                : entityManager.getCriteriaBuilder().asc(sortBy);
        query.orderBy(order);
        return entityManager
                .createQuery(query)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList()
                .toArray(Item[]::new);
    }
}
