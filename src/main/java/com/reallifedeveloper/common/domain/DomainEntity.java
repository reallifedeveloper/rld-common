package com.reallifedeveloper.common.domain;

import java.io.Serializable;

/**
 * A domain-driven design entity, i.e., an object that is not defined by its attributes,
 * but rather by a thread of continuity and its identity.
 *
 * @author RealLifeDeveloper
 *
 * @param <T> the type of entity
 * @param <ID> the type of object used to identify the entity
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface DomainEntity<T, ID extends Serializable> extends DomainObject<T> {

    /**
     * Gives the identity of this entity.
     *
     * @return the identity
     */
    ID id();

}
