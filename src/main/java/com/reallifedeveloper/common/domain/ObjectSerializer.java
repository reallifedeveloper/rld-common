package com.reallifedeveloper.common.domain;

import java.io.Serializable;

/**
 * A serializer of objects, i.e., a class that can convert an arbitrary object to a serialized form and back.
 *
 * @author RealLifeDeveloper
 *
 * @param <T> the type of the serialized form
 */
public interface ObjectSerializer<T extends Serializable> {

    /**
     * Converts an object to its serialized form.
     * <p>
     * The implementation should support serializing <code>null</code> values.
     *
     * @param object the object to serialize, can be <code>null</code>
     *
     * @return the serialized representation of <code>object</code>
     */
    T serialize(Object object);

    /**
     * Given the serialized representation of an object, converts it back to the object.
     *
     * @param serializedObject the serialized form of an object
     * @param objectType the concrete class of the serialized object
     * @param <U> the type of the object
     *
     * @return the object represented by <code>serializedObject</code>
     *
     * @throws IllegalArgumentException if any argument is <code>null</code>
     */
    <U> U deserialize(T serializedObject, Class<U> objectType);

}
