package com.reallifedeveloper.common.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link DomainEventSubscriber} that logs all events using Slf4j.
 *
 * @author RealLifeDeveloper
 */
public class LoggingDomainEventSubscriber implements DomainEventSubscriber<DomainEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingDomainEventSubscriber.class);

    @Override
    public void handleEvent(DomainEvent event) {
        LOG.info(event.toString());
    }

    @Override
    public Class<? extends DomainEvent> eventType() {
        return DomainEvent.class;
    }

}
