package com.reallifedeveloper.common.infrastructure;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import com.reallifedeveloper.common.application.notification.NotificationReader;

/**
 * An implementation of the {@link NotificationReader} interface that works with JSON as the serialized
 * form, using <a href="https://code.google.com/p/google-gson/">Gson</a> to parse the JSON string.
 *
 * @author RealLifeDeveloper
 */
public class GsonNotificationReader implements NotificationReader {

    private final JsonObject jsonObject;
    private final DateFormat dateFormat = new SimpleDateFormat(GsonObjectSerializer.DATE_FORMAT);

    /**
     * Creates a new <code>GsonNotificationReader</code> that parses the given JSON-serialized notification.
     *
     * @param jsonNotification the JSON representation of the notification to read
     *
     * @throws IllegalArgumentException if <code>jsonNotification</code> is <code>null</code> or not a valid
     * JSON object
     */
    public GsonNotificationReader(String jsonNotification) {
        if (jsonNotification == null) {
            throw new IllegalArgumentException("jsonNotification must not be null");
        }
        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(jsonNotification);
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Not a JSON object: " + jsonNotification);
            }
            this.jsonObject = element.getAsJsonObject();
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Not legal JSON: " + jsonNotification);
        }
    }

    @Override
    public String eventType() {
        return stringValue(notification(), "eventType", true);
    }

    @Override
    public long storedEventId() {
        return longValue(notification(), "storedEventId", true);
    }

    @Override
    public Date occurredOn() {
        return dateValue(notification(), "occurredOn", true);
    }

    @Override
    public int eventVersion() {
        return intValue(event(), "version", true);
    }

    @Override
    public Integer eventIntValue(String fieldName) {
        return intValue(event(), fieldName, false);
    }

    @Override
    public Long eventLongValue(String fieldName) {
        return longValue(event(), fieldName, false);
    }

    @Override
    public Double eventDoubleValue(String fieldName) {
        return doubleValue(event(), fieldName, false);
    }

    @Override
    public String eventStringValue(String fieldName) {
        return stringValue(event(), fieldName, false);
    }

    @Override
    public Date eventDateValue(String fieldName) {
        return dateValue(event(), fieldName, false);
    }

    private Integer intValue(JsonObject object, String fieldName, boolean required) {
        JsonElement jsonElement = fieldValue(object, fieldName, required);
        return isNull(jsonElement) ? null : jsonElement.getAsInt();
    }

    private Long longValue(JsonObject object, String fieldName, boolean required) {
        JsonElement jsonElement = fieldValue(object, fieldName, required);
        return isNull(jsonElement) ? null : jsonElement.getAsLong();
    }

    private Double doubleValue(JsonObject object, String fieldName, boolean required) {
        JsonElement jsonElement = fieldValue(object, fieldName, required);
        return isNull(jsonElement) ? null : jsonElement.getAsDouble();
    }

    private String stringValue(JsonObject object, String fieldName, boolean required) {
        JsonElement jsonElement = fieldValue(object, fieldName, required);
        return isNull(jsonElement) ? null : jsonElement.getAsString();
    }

    private Date dateValue(JsonObject object, String fieldName, boolean required) {
        JsonElement jsonElement = fieldValue(object, fieldName, required);
        try {
            return isNull(jsonElement) ? null : dateFormat.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Field " + fieldName + " could not be parsed as a date ("
                    + GsonObjectSerializer.DATE_FORMAT + "): object=" + object);
        }
    }

    /**
     * Gives the value of a, potentially nested, field in a <code>JsonObject</code>. The field name can
     * be simple, e.g., "foo", or nested, e.g., "foo.bar". If the name is nested, the sub-components should
     * be the names of nested objects.
     * <p>
     * For example, if <code>fieldName</code> is "foo.bar.baz", the object "foo" is first looked up in
     * <code>object</code>, then the object "bar" is looked up in the result, and finally the value of the
     * field "baz" in the resulting object is returned.
     *
     * @param rootObject the <code>JsonObject</code> to use to look up the value of the field
     * @param fieldName the name of the field to look up, potentially nested, e.g., "foo.bar"
     * @param required if <code>true</code>, throws an exception if the field does not exist
     *
     * @return the value of the, potentially nested, field as a <code>JsonElement</code>
     *
     * @throws IllegalArgumentException if <code>required</code> is <code>true</code> and the field does not exist
     */
    private JsonElement fieldValue(JsonObject rootObject, String fieldName, boolean required) {
        JsonObject object = rootObject;
        String[] fieldNames = fieldName.split("\\.");
        for (int i = 0; i < fieldNames.length - 1; i++) {
            JsonElement element = object.get(fieldNames[i]);
            if (element == null) {
                throw new IllegalArgumentException("Field " + fieldName + " not found: object=" + rootObject);
            }
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Field " + fieldName + " not an object: object=" + rootObject);
            }
            object = object.get(fieldNames[i]).getAsJsonObject();
        }
        JsonElement jsonElement = object.get(fieldNames[fieldNames.length - 1]);
        if (required && jsonElement == null) {
            throw new IllegalArgumentException("Field " + fieldName + " not found: object=" + rootObject);
        }
        return jsonElement;
    }

    private JsonObject notification() {
        return jsonObject;
    }

    private JsonObject event() {
        if (isNull(jsonObject.get("event"))) {
            throw new IllegalArgumentException("event not found in JSON string");
        }
        return jsonObject.get("event").getAsJsonObject();
    }

    private boolean isNull(JsonElement jsonElement) {
        return jsonElement == null || jsonElement == JsonNull.INSTANCE;
    }
}
