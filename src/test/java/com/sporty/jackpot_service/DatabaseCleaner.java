package com.sporty.jackpot_service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    /**
     * Discovers all database table names dynamically from JPA Metadata on startup.
     * This avoids hardcoding table names and handles any schema changes automatically.
     */
    private synchronized void discoverTableNames() {
        if (tableNames == null) {
            tableNames = entityManager.getMetamodel().getEntities().stream()
                    .map(EntityType::getJavaType)
                    .filter(javaType -> javaType.isAnnotationPresent(Table.class))
                    .map(javaType -> javaType.getAnnotation(Table.class).name())
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void truncateAllTables() {
        discoverTableNames();

        // 1. Temporarily disable foreign key constraints so execution order doesn't matter
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // 2. Loop through and execute production-grade truncation with sequence restarts
        for (String tableName : tableNames) {
            // RESTART IDENTITY resets auto-increment long values back to 1 for the next test run
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " RESTART IDENTITY").executeUpdate();
        }

        // 3. Re-enable foreign key checks to keep data safety active for the next test execution
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
