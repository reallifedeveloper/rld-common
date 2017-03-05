package com.reallifedeveloper.common.domain;

import java.io.Serializable;

/**
 * A mechanism for encapsulating storage, retrieval, and search behavior which
 * emulates a collection of objects.
 *
 * @author RealLifeDeveloper
 *
 * @param <T> the type of objects managed by the repository
 * @param <ID> the type used to identify the objects
 */
public interface Repository<T, ID extends Serializable> {

    /**
     * Gives the entity with the given id.
     *
     * @param id the id of the entity to find
     * @return the entity with the given id, or <code>null</code> if no such entity
     * could be found
     */
    T findById(ID id);

}
