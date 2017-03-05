package com.reallifedeveloper.common.domain;

/**
 * A domain-driven design value object, i.e., an immutable object that contains attributes
 * but has no conceptual identity.
 *
 * @author RealLifeDeveloper
 *
 * @param <T> the type of value object
 */
public interface ValueObject<T> extends DomainObject<T> {

    /**
     * Checks if this value object has the same value as another.
     *
     * @param otherObject the other value object
     * @return <code>true</code> if this object has the same value as <code>otherObject</code>,
     * <code>false</code> otherwise
     */
    boolean hasSameValueAs(T otherObject);

}
