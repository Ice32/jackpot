package com.sporty.jackpot_service;

import org.jspecify.annotations.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class TestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(@NonNull TestContext testContext) {
        DatabaseCleaner databaseCleaner = testContext.getApplicationContext().getBean(DatabaseCleaner.class);

        // Execute the full whole-DB truncation pass
        databaseCleaner.truncateAllTables();
    }
}
