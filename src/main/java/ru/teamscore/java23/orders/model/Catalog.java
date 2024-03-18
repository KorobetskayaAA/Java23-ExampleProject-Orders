package ru.teamscore.java23.orders.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.enums.CatalogSortOption;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Catalog {
    private final EntityManager entityManager;

    public long getItemsCount() {
        return entityManager
                .createNamedQuery("itemsCount", Long.class)
                .getSingleResult();
    }

    public Optional<Item> getItem(@NonNull Barcode barcode) {
        try {
            return Optional.of(entityManager
                    .createNamedQuery("itemByBarcode", Item.class)
                    .setParameter("barcode", barcode)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Item> getItem(@NonNull String barcode) {
        return getItem(new Barcode(barcode));
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

    public void updateItem(@NonNull Item item) {
        entityManager.getTransaction().begin();
        entityManager.merge(item);
        entityManager.getTransaction().commit();
    }

    public long getCount(String search) {
        String pattern = "%" + search + "%";
        return entityManager
                .createQuery("select count(*) from Item " +
                                "where title ilike :pattern or " +
                                "cast(barcode as String) ilike :pattern",
                        Long.class)
                .setParameter("pattern", pattern)
                .getSingleResult();
    }

    public List<Item> getSorted(CatalogSortOption option, boolean desc, String search, int page, int pageSize) {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(Item.class);
        Root<Item> root = query.from(Item.class);

        if (search != null && !search.equals("")) {
            var searchBarcode = cb.equal(root.get("barcode"), search);
            var searchTitle = cb.like(root.get("title"), "%" + search + "%");
            query.where(cb.or(searchBarcode, searchTitle));
        }

        if (option != null) {
            var sortBy = root.get(option.getColumnName());
            var order = desc
                    ? cb.desc(sortBy)
                    : cb.asc(sortBy);
            query.orderBy(order);
        }

        return entityManager
                .createQuery(query)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
}
