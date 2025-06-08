package com.reallifedeveloper.common.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.domain.ClockTimeService;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistry;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistryTest;

public class TimestampedJpaEntityTest {

    private final ZonedDateTime testDateTime = ZonedDateTime.now();

    @BeforeAll
    public static void initClass() {
        CommonDomainRegistryTest.initCommonDomainRegistry();
    }

    @BeforeEach
    public void init() {
        ClockTimeService timeService = (ClockTimeService) CommonDomainRegistry.timeService();
        timeService.setClock(Clock.fixed(testDateTime.toInstant(), testDateTime.getZone()));
    }

    @Test
    public void constructor() {
        TestTimestampedJpaEntity entity = new TestTimestampedJpaEntity();
        assertFalse(entity.id().isPresent(), "ID should be not be present");
        assertFalse(entity.created().isPresent(), "Created timestamp should not be present");
        assertFalse(entity.updated().isPresent(), "Updated timestamp should not be present");
    }

    @Test
    public void constructorId() {
        long id = 42;
        TestTimestampedJpaEntity entity = new TestTimestampedJpaEntity(id);
        assertEquals(id, entity.id().get().longValue(), "Wrong ID");
        assertEquals(testDateTime, entity.created().get(), "Wrong created date");
        assertEquals(testDateTime, entity.updated().get(), "Wrong updated date");
    }

    @Test
    public void constructorIdCreatedUpdated() {
        long id = 42;
        ZonedDateTime created = ZonedDateTime.now();
        ZonedDateTime updated = created.plusSeconds(1);
        TestTimestampedJpaEntity entity = new TestTimestampedJpaEntity(id, created, updated);
        assertEquals(id, entity.id().get().longValue(), "Wrong ID");
        assertEquals(created, entity.created().get(), "Wrong created date");
        assertEquals(updated, entity.updated().get(), "Wrong updated date");
    }

    @Test
    public void constructorCreatedNull() {
        assertThrows(IllegalArgumentException.class, () -> new TestTimestampedJpaEntity(42L, null, ZonedDateTime.now()),
                "Expected IllegalArgumentException for null created date");
    }

    @Test
    public void constructorUpdatedNull() {
        assertThrows(IllegalArgumentException.class, () -> new TestTimestampedJpaEntity(42L, ZonedDateTime.now(), null),
                "Expected IllegalArgumentException for null updated date");
    }

    @Test
    public void setUpdated() {
        ZonedDateTime updated = ZonedDateTime.now();
        TestTimestampedJpaEntity entity = new TestTimestampedJpaEntity();
        assertFalse(entity.updated().isPresent(), "Updated timestamp should not be present");
        entity.setUpdated(updated);
        assertEquals(updated, entity.updated().get(), "Wrong updated date");
    }

    @Test
    public void setUpdatedNull() {
        TestTimestampedJpaEntity entity = new TestTimestampedJpaEntity();
        assertThrows(IllegalArgumentException.class, () -> entity.setUpdated(null),
                "Expected IllegalArgumentException for setting null updated date");
    }

    private static class TestTimestampedJpaEntity extends TimestampedJpaEntity<Long> {

        TestTimestampedJpaEntity() {
            super();
        }

        TestTimestampedJpaEntity(Long id) {
            super(id);
        }

        TestTimestampedJpaEntity(Long id, ZonedDateTime created, ZonedDateTime updated) {
            super(id, created, updated);
        }
    }
}
