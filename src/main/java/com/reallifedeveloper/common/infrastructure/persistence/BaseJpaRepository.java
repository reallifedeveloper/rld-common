package com.reallifedeveloper.common.infrastructure.persistence;

import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

/**
 * Base class for implementations of repositories that use JPA but that cannot use Spring Data, e.g., because they work with more than one
 * entity type.
 *
 * @author RealLifeDeveloper
 */
public class BaseJpaRepository {

    /**
     * Creates a new {@code BaseJpaRepository}, intended to be used by sub-classes.
     */
    protected BaseJpaRepository() {
        // The only constructor is protected, to disallow direct instantiation.
    }

    /**
     * Gives the value of the {@code @Query} annotation on the given method. The annotation can be on the method in the class, or on the
     * method in an implemented interface.
     *
     * @param methodName     the name of the method with the {@code @Query} annotation
     * @param parameterTypes the classes of the method parameters
     *
     * @return the query string, i.e., the value of the {@code @Query} annotation, or an empty Optional if no such annotation can be found
     */
    protected Optional<String> getQueryString(String methodName, Class<?>... parameterTypes) {
        try {
            return findQueryAnnotation(methodName, parameterTypes).map(Query::value);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unexpected error, this should never occur", e);
        }
    }

    @SuppressWarnings({ "PMD.EmptyCatchBlock", "PMD.AvoidAccessibilityAlteration" })
    private Optional<Query> findQueryAnnotation(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        Query query = method.getAnnotation(Query.class);
        if (query == null) {
            for (Class<?> c : getClass().getInterfaces()) {
                try {
                    method = c.getDeclaredMethod(methodName, parameterTypes);
                    method.setAccessible(true);
                    query = method.getDeclaredAnnotation(Query.class);
                    if (query != null) {
                        break;
                    }
                } catch (NoSuchMethodException e) {
                    // Ignore, try next interface (if there is one)
                }
            }
        }
        return Optional.ofNullable(query);
    }
}
