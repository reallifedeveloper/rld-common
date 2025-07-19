package com.reallifedeveloper.common.infrastructure;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import com.reallifedeveloper.common.application.notification.NotificationReader;
import com.reallifedeveloper.common.domain.ErrorHandling;

/**
 * An implementation of the {@link NotificationReader} interface that works with JSON as the serialized form, using
 * <a href="https://code.google.com/p/google-gson/">Gson</a> to parse the JSON string.
 *
 * @author RealLifeDeveloper
 */
@SuppressWarnings("PMD.UnnecessaryCast") // We use casts to @NonNull that PMD considers unnecessary.
public final class GsonNotificationReader implements NotificationReader {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(GsonObjectSerializer.DATE_TIME_FORMAT);

    private final JsonObject notification;
    private final JsonObject event;

    /**
     * Creates a new {@code GsonNotificationReader} that parses the given JSON-serialized notification.
     *
     * @param jsonNotification the JSON representation of the notification to read
     *
     * @throws IllegalArgumentException if {@code jsonNotification} is {@code null} or not a valid JSON object
     */
    public GsonNotificationReader(String jsonNotification) {
        ErrorHandling.checkNull("jsonNotification must not be null", jsonNotification);
        try {
            JsonElement element = JsonParser.parseString(jsonNotification);
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Not a JSON object: " + jsonNotification);
            }
            this.notification = element.getAsJsonObject();
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Not legal JSON: " + jsonNotification, e);
        }
        if (JsonUtil.isNull(notification.get("event"))) {
            throw new IllegalArgumentException("event not found in JSON string: " + jsonNotification);
        }
        this.event = notification.get("event").getAsJsonObject();
    }

    @Override
    public String eventType() {
        return (@NonNull String) JsonUtil.stringValue(notification, "eventType", true);
    }

    @Override
    public Long storedEventId() {
        return (@NonNull Long) JsonUtil.longValue(notification, "storedEventId", true);
    }

    @Override
    public ZonedDateTime occurredOn() {
        return (@NonNull ZonedDateTime) JsonUtil.zonedDateTimeValue(notification, "occurredOn", true);
    }

    @Override
    public Integer eventVersion() {
        return (@NonNull Integer) JsonUtil.intValue(event, "eventVersion", true);
    }

    @Override
    public Optional<Integer> eventIntValue(String fieldName) {
        return Optional.ofNullable(JsonUtil.intValue(event, fieldName, false));
    }

    @Override
    public Optional<Long> eventLongValue(String fieldName) {
        return Optional.ofNullable(JsonUtil.longValue(event, fieldName, false));
    }

    @Override
    public Optional<Double> eventDoubleValue(String fieldName) {
        return Optional.ofNullable(JsonUtil.doubleValue(event, fieldName, false));
    }

    @Override
    public Optional<String> eventStringValue(String fieldName) {
        return Optional.ofNullable(JsonUtil.stringValue(event, fieldName, false));
    }

    @Override
    public Optional<ZonedDateTime> zonedDateTimeValue(String fieldName) {
        return Optional.ofNullable(JsonUtil.zonedDateTimeValue(event, fieldName, false));
    }

    private static final class JsonUtil {

        private static @Nullable Integer intValue(JsonObject object, String fieldName, boolean required) {
            JsonElement jsonElement = fieldValue(object, fieldName, required);
            return isNull(jsonElement) ? null : jsonElement.getAsInt();
        }

        private static @Nullable Long longValue(JsonObject object, String fieldName, boolean required) {
            JsonElement jsonElement = fieldValue(object, fieldName, required);
            return isNull(jsonElement) ? null : jsonElement.getAsLong();
        }

        private static @Nullable Double doubleValue(JsonObject object, String fieldName, boolean required) {
            JsonElement jsonElement = fieldValue(object, fieldName, required);
            return isNull(jsonElement) ? null : jsonElement.getAsDouble();
        }

        private static @Nullable String stringValue(JsonObject object, String fieldName, boolean required) {
            JsonElement jsonElement = fieldValue(object, fieldName, required);
            return isNull(jsonElement) ? null : jsonElement.getAsString();
        }

        private static @Nullable ZonedDateTime zonedDateTimeValue(JsonObject object, String fieldName, boolean required) {
            JsonElement jsonElement = fieldValue(object, fieldName, required);
            return isNull(jsonElement) ? null : ZonedDateTime.parse(jsonElement.getAsString(), DATE_TIME_FORMATTER);
        }

        /**
         * Gives the value of a, potentially nested, field in a {@code JsonObject}. The field name can be simple, e.g., "foo", or nested,
         * e.g., "foo.bar". If the name is nested, the sub-components should be the names of nested objects.
         * <p>
         * For example, if {@code fieldName} is "foo.bar.baz", the object "foo" is first looked up in {@code object}, then the object "bar"
         * is looked up in the result, and finally the value of the field "baz" in the resulting object is returned.
         * <p>
         * If {@code required} is {@code true}, this method never returns {@code null}.
         *
         * @param rootObject the {@code JsonObject} to use to look up the value of the field
         * @param fieldName  the name of the field to look up, potentially nested, e.g., "foo.bar"
         * @param required   if {@code true}, throws an exception if the field does not exist or is {@code null}
         *
         * @return the value of the, potentially nested, field as a {@code JsonElement}
         *
         * @throws IllegalArgumentException if {@code required} is {@code true} and the field does not exist or is {@code null}
         */
        private static JsonElement fieldValue(JsonObject rootObject, String fieldName, boolean required) {
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
                object = element.getAsJsonObject();
            }
            JsonElement jsonElement = object.get(fieldNames[fieldNames.length - 1]);
            if (required && isNull(jsonElement)) {
                throw new IllegalArgumentException("Field " + fieldName + " is missing or null: object=" + rootObject);
            }
            return jsonElement;
        }

        private static boolean isNull(JsonElement jsonElement) {
            return jsonElement == null || jsonElement == JsonNull.INSTANCE;
        }
    }
}
