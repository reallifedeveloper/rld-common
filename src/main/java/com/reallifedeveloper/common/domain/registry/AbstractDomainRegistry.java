package com.reallifedeveloper.common.domain.registry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Base class for Spring-based domain registries. A domain registry is conceptually a singleton
 * and should only be used when dependency injection cannot be used, e.g., in entities or value
 * objects.
 *
 * @author RealLifeDeveloper
 */
public abstract class AbstractDomainRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * Looks up the Spring bean of the given type. This method never returns <code>null</code>; if the
     * bean cannot be found, an exception is thrown.
     *
     * @param <T> the type of the bean to look up
     * @param beanType the class of the bean to look up
     *
     * @return the Spring bean, never <code>null</code>
     */
    protected static <T> T getBean(Class<T> beanType) {
        if (applicationContext == null) {
            throw new IllegalStateException("DomainRegistry has not been initialized");
        }
        return applicationContext.getBean(beanType);
    }

    @Override
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
            justification = "The domain registry is a singleton, and it is left to Spring to handle concurrency issues")
    public synchronized void setApplicationContext(ApplicationContext applicationContext) {
        AbstractDomainRegistry.applicationContext = applicationContext;
    }
}
