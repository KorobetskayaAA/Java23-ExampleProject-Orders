package ru.teamscore.java23.orders.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SqlScripts {
    @Transactional
    public static void runFromFile(EntityManagerFactory entityManagerFactory, String filePath) throws IOException {
        String sqlScript = new String(Files.readAllBytes(Path.of("src", "main", "scripts", "db", filePath)));
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Query query = entityManager.createNativeQuery(sqlScript);
            entityManager.getTransaction().begin();
            query.executeUpdate();
            entityManager.getTransaction().commit();
        }
    }
}
