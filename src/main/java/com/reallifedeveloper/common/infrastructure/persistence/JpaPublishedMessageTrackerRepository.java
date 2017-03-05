package com.reallifedeveloper.common.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reallifedeveloper.common.application.notification.PublishedMessageTracker;
import com.reallifedeveloper.common.application.notification.PublishedMessageTrackerRepository;

/**
 * A Spring Data JPA "implementation" of the {@link PublishedMessageTrackerRepository} interface.
 *
 * @author RealLifeDeveloper
 */
public interface JpaPublishedMessageTrackerRepository extends PublishedMessageTrackerRepository,
        JpaRepository<PublishedMessageTracker, Long> {

}
