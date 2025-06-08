package com.reallifedeveloper.common.domain.registry;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Base class for Spring-based domain registries. A domain registry is conceptually a singleton and should only be used when dependency
 * injection cannot be used, e.g., in entities or value objects.
 *
 * @author RealLifeDeveloper
 */
public abstract class AbstractDomainRegistry implements ApplicationContextAware {

    private static final Lock CLASS_LOCK = new ReentrantLock();

    private static @Nullable ApplicationContext applicationContext;

    /**
     * Looks up the Spring bean of the given type. This method never returns {@code null}; if the bean cannot be found, an exception is
     * thrown.
     *
     * @param <T>      the type of the bean to look up
     * @param beanType the class of the bean to look up
     *
     * @return the Spring bean, never {@code null}
     */
    protected static <T> T getBean(Class<T> beanType) {
        if (applicationContext == null) {
            throw new IllegalStateException("DomainRegistry has not been initialized");
        }
        return applicationContext.getBean(beanType);
    }

    @Override
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "We handle concurrency issues with a lock")
    public void setApplicationContext(ApplicationContext applicationContext) {
        CLASS_LOCK.lock();
        try {
            AbstractDomainRegistry.applicationContext = applicationContext;
        } finally {
            CLASS_LOCK.unlock();
        }
    }
}
