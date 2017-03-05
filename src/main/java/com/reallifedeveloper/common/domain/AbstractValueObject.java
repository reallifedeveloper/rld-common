package com.reallifedeveloper.common.domain;

/**
 * An abstract base class that should be used when creating value objects.
 * <p>
 * It provides no extra functionality, but reminds the implementor to add implementations of {@link #hashCode()},
 * {@link #equals(Object)} and {@link #toString()} to the value object class.
 *
 * @author RealLifeDeveloper
 *
 * @param <T> the type of value object
 */
public abstract class AbstractValueObject<T> implements ValueObject<T> {

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();

}
