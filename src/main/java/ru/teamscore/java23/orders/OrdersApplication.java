package ru.teamscore.java23.orders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.entities.OrderItem;
import ru.teamscore.java23.orders.model.entities.OrderWithItems;
import ru.teamscore.java23.orders.model.enums.ItemStatus;
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
        var conf = new Configuration()
                .configure("hibernate.cfg.xml")
                .setProperty("hibernate.connection.url", System.getenv("POSTGRES_URL"))
                .setProperty("hibernate.connection.username", System.getenv("POSTGRES_USER"))
                .setProperty("hibernate.connection.password", System.getenv("POSTGRES_PASSWORD"));
        return conf
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
        var modelMapper = new ModelMapper();
        Converter<String, ItemStatus> itemStatusConverter = new AbstractConverter<>() {
            protected ItemStatus convert(String source) {
                return source == null ? null : ItemStatus.valueOf(source);
            }
        };
        Converter<String, Barcode> barcodeConverter = new AbstractConverter<>() {
            protected Barcode convert(String source) {
                return source == null ? null : new Barcode(source);
            }
        };

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                // LOOSE - мягкое связывание для вложенной сущности Item
                // (проверяет совпадение по имени поля)
                // При этом price есть и в OrderItem, и в Item - будет взят из "верхней" сущности OrderItem        modelMapper.addConverter(itemStatusConverter);
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        modelMapper.addConverter(barcodeConverter);
        return modelMapper;
    }
}
