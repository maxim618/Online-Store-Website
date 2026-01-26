package com.ecommerce.abstractTestClasses;

import com.ecommerce.testutil.DbCleaner;
import com.ecommerce.testutil.ValkeyTestCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
/**
 * Base class for tests that require fully isolated database state.
 * Use this for all tests that modify domain aggregates (orders, payments, security).
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractFullDatabaseCleanupTest {

    @Autowired
    DbCleaner dbCleaner;

    @Autowired(required = false)
    ValkeyTestCleaner valkeyTestCleaner;

    @BeforeEach
    void cleanState() {
        dbCleaner.clean();
        if (valkeyTestCleaner != null) {
            valkeyTestCleaner.clearAll();
        }
    }
}