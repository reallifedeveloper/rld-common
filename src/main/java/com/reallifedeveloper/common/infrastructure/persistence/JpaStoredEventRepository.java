package com.reallifedeveloper.common.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.application.eventstore.StoredEventRepository;

/**
 * A Spring Data JPA "implementation" of the {@link StoredEventRepository} interface.
 *
 * @author RealLifeDeveloper
 */
public interface JpaStoredEventRepository extends StoredEventRepository, JpaRepository<StoredEvent, Long> {

    @Override
    @Query("select se from StoredEvent se where se.id > :firstStoredEventId")
    List<StoredEvent> allEventsSince(@Param("firstStoredEventId") long firstStoredEventId);

    @Override
    @Query("select se from StoredEvent se where se.id between :firstStoredEventId and :lastStoredEventId")
    List<StoredEvent> allEventsBetween(@Param("firstStoredEventId") long firstStoredEventId,
            @Param("lastStoredEventId") long lastStoredEventId);

    @Override
    @Query("select max(se.id) from StoredEvent se")
    Optional<Long> lastStoredEventId();
}
