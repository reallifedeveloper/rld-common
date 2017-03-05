package com.reallifedeveloper.common.application.notification;

import com.reallifedeveloper.tools.test.database.inmemory.InMemoryJpaRepository;
import com.reallifedeveloper.tools.test.database.inmemory.LongPrimaryKeyGenerator;

public class InMemoryPublishedMessageTrackerRepository extends InMemoryJpaRepository<PublishedMessageTracker, Long>
        implements PublishedMessageTrackerRepository {

    public InMemoryPublishedMessageTrackerRepository() {
        super(new LongPrimaryKeyGenerator());
    }

    @Override
    public PublishedMessageTracker findByPublicationChannel(String publicationChannel) {
        return findByUniqueField("publicationChannel", publicationChannel);
    }

}
