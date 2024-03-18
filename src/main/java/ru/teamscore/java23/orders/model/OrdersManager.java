package ru.teamscore.java23.orders.model;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.teamscore.java23.orders.model.entities.OrderItem;
import ru.teamscore.java23.orders.model.entities.OrderWithItems;
import ru.teamscore.java23.orders.model.enums.OrderStatus;
import ru.teamscore.java23.orders.model.statistics.OrdersStatistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrdersManager {
    private final EntityManager entityManager;

    @Getter()
    private final OrderInfoManager info = new OrderInfoManager();

    public long getOrdersCount() {
        return entityManager
                .createQuery("select count(*) from OrderWithItems", Long.class)
                .getSingleResult();
    }

    public OrderWithItems[] getOrdersAll() {
        return entityManager
                .createQuery("from OrderWithItems order by id", OrderWithItems.class)
                .getResultList()
                .toArray(OrderWithItems[]::new);
    }

    public Optional<OrderWithItems> getOrder(long id) {
        return Optional.of(
                entityManager.find(OrderWithItems.class, id)
        );
    }

    public void addOrder(OrderWithItems order) {
        var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(order);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void updateOrder(@NonNull OrderWithItems order) {
        entityManager.getTransaction().begin();
        entityManager.merge(order);
        entityManager.getTransaction().commit();
    }

    public class OrderInfoManager {
        public BigDecimal getProcessingTotalAmount() {
            var result = entityManager
                    .createQuery("select sum(oi.price * oi.quantity) from OrderItem oi " +
                                    "where oi.pk.order.order.status = :status",
                            BigDecimal.class)
                    .setParameter("status", OrderStatus.PROCESSING)
                    .getSingleResult();
            return (result == null) ? BigDecimal.ZERO : result.setScale(2);
        }

        public List<OrderItem> getProcessingOrderItems() {
            return entityManager
                    .createQuery("select distinct oi from OrderItem as oi " +
                                    "where oi.pk.order.order.status = :status",
                            OrderItem.class)
                    .setParameter("status", OrderStatus.PROCESSING)
                    .getResultList();
        }

        public List<OrdersStatistics> getStatistics(LocalDate from, LocalDate to) {
            return entityManager
                    .createQuery("from OrdersStatistics where month between :from and :to",
                            OrdersStatistics.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList()
                    .stream().toList();
        }
    }
}
