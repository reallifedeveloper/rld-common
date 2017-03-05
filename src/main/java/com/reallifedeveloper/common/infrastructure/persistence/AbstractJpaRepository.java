package com.reallifedeveloper.common.infrastructure.persistence;

import java.lang.reflect.Method;

import org.springframework.data.jpa.repository.Query;

/**
 * Base class for implementations of repositories that use JPA but that cannot
 * use Spring Data, e.g., because they work with more than one entity type.
 *
 * @author RealLifeDeveloper
 */
public abstract class AbstractJpaRepository {

    /**
     * Gives the value of the <code>@Query</code> annotation on the given method. The
     * annotation can be on the method in the class, or on the method in an implemented
     * interface.
     *
     * @param methodName the name of the method with the <code>@Query</code> annotation
     * @param parameterTypes the classes of the method parameters
     * @return the query string, i.e., the value of the <code>@Query</code> annotation,
     * or <code>null</code> if no such annotation can be found
     */
    protected String getQueryString(String methodName, Class<?> ... parameterTypes) {
        String queryString = null;
        try {
            Method method = getClass().getMethod(methodName, parameterTypes);
            Query query = method.getAnnotation(Query.class);
            if (query == null) {
                for (Class<?> c : getClass().getInterfaces()) {
                    try {
                        method = c.getMethod(methodName, parameterTypes);
                        query = method.getAnnotation(Query.class);
                        if (query != null) {
                            break;
                        }
                    } catch (NoSuchMethodException e) {
                        // Ignore, try next interface (if there is one)
                    }
                }
            }
            if (query != null) {
                queryString = query.value();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error, this should never occur", e);
        }
        return queryString;
    }

}
