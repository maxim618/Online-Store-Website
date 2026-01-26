package com.ecommerce.abstractTestClasses;

import com.ecommerce.testutil.DbCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
/**
 * Base class for tests that rely on pre-existing reference data
 * and do not mutate shared aggregates.
 *
 * DO NOT use for payment/order tests.
 */
public abstract class AbstractPartialDatabaseCleanupTest {
    @SpringBootTest
    @ActiveProfiles("test")
    public abstract class FullAbstractDatabaseCleaner {

        @Autowired
        DbCleaner dbCleaner;

        @BeforeEach
        void cleanState() {
            dbCleaner.clean();
        }
    }
}
