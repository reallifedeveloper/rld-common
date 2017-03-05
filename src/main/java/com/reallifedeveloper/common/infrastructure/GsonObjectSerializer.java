package com.reallifedeveloper.common.infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * An implementation of the {@link ObjectSerializer} that uses JSON as the serialized form.
 *
 * @author RealLifeDeveloper
 */
public class GsonObjectSerializer implements ObjectSerializer<String> {

    /**
     * The format used to store <code>java.util.Date</code> objects. The string is a pattern that can be
     * used by <code>SimpleDateFormatter</code>.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss:SSSZ";

    private final Gson gson;

    /**
     * Creates a new <code>GsonObjectSerializer</code> with default values.
     * <p>
     * The default values include using the pattern {@value #DATE_FORMAT} when working with
     * <code>java.util.Date</code> objects.
     */
    public GsonObjectSerializer() {
        gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
    }

    @Override
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <U> U deserialize(String serializedObject, Class<U> objectType) {
        if (serializedObject == null || objectType == null) {
            throw new IllegalArgumentException("Arguments must not be null: serializedEvent=" + serializedObject
                    + ", eventType=" + objectType);
        }
        try {
            return gson.fromJson(serializedObject, objectType);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("serializedEvent cannot be parsed as JSON: " + serializedObject, e);
        }
    }

}
