package com.reallifedeveloper.common.application.eventstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.reallifedeveloper.tools.test.database.inmemory.InMemoryJpaRepository;
import com.reallifedeveloper.tools.test.database.inmemory.LongPrimaryKeyGenerator;

public class InMemoryStoredEventRepository extends InMemoryJpaRepository<StoredEvent, Long>
implements StoredEventRepository {

    public InMemoryStoredEventRepository() {
        super(new LongPrimaryKeyGenerator());
    }

    @Override
    public List<StoredEvent> allEventsSince(long storedEventId) {
        List<StoredEvent> events = new ArrayList<>();
        for (StoredEvent event : findAll()) {
            if (event.id() > storedEventId) {
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public List<StoredEvent> allEventsBetween(long firstStoredEventId, long lastStoredEventId) {
        List<StoredEvent> events = new ArrayList<>();
        for (StoredEvent event : findAll()) {
            if (firstStoredEventId <= event.id() && event.id() <= lastStoredEventId) {
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public Optional<Long> lastStoredEventId() {
        if (count() == 0) {
            return Optional.empty();
        }
        Long lastStoredEventId = -1L;
        for (StoredEvent event : findAll()) {
            if (event.id() > lastStoredEventId) {
                lastStoredEventId = event.id();
            }
        }
        return Optional.of(lastStoredEventId);
    }

}
