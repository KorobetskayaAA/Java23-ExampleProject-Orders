package ru.teamscore.java23.orders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.entities.OrderItem;
import ru.teamscore.java23.orders.model.entities.OrderWithItems;
import ru.teamscore.java23.orders.model.statistics.OrdersStatistics;

@SpringBootApplication
public class OrdersApplication {
    public static void main(String[] args) {
        // RestartClassLoader иногда вызывает конфликты классов при использовании java.util.stream
        // из-за загрузки в два разных class loader. Пример ошибки:
        // class ru.teamscore.java23.orders.model.statistics.OrdersStatistics cannot be cast
        // to class ru.teamscore.java23.orders.model.statistics.OrdersStatistics
        // (ru.teamscore.java23.orders.model.statistics.OrdersStatistics is in unnamed module of loader 'app';
        // ru.teamscore.java23.orders.model.statistics.OrdersStatistics is in unnamed module of loader
        // org.springframework.boot.devtools.restart.classloader.RestartClassLoader @61589606)
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(OrdersApplication.class, args);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Item.class)
                .addAnnotatedClass(OrderWithItems.class)
                .addAnnotatedClass(OrderItem.class)
                .addAnnotatedClass(OrdersStatistics.class)
                .buildSessionFactory();
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
