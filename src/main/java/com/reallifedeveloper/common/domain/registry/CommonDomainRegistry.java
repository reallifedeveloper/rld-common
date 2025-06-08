package com.reallifedeveloper.common.domain.registry;

import com.reallifedeveloper.common.domain.TimeService;
import com.reallifedeveloper.common.domain.event.DomainEventPublisher;

/**
 * A registry of common domain components that need to be available in code where it is not
 * practical to use dependency injection.
 *
 * @author RealLifeDeveloper
 */
public class CommonDomainRegistry extends AbstractDomainRegistry {

    /**
     * Gives the {@link DomainEventPublisher} to use to publish domain events.
     *
     * @return the {@code DomainEventPublisher}
     */
    public static DomainEventPublisher domainEventPublisher() {
        return getBean(DomainEventPublisher.class);
    }

    /**
     * Gives the {@link TimeService} to use to work with "current" time.
     *
     * @return the {@code TimeService}
     */
    public static TimeService timeService() {
        return getBean(TimeService.class);
    }
}
