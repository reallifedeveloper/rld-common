package com.reallifedeveloper.common.infrastructure;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;

/**
 * An implementation of the {@link ObjectSerializer} that uses JSON as the serialized form.
 *
 * @author RealLifeDeveloper
 */
public class GsonObjectSerializer implements ObjectSerializer<String> {

    /**
     * The format used to parse and format date objects. The string is a pattern that can be used by a {@code java.time.DateTimeFormatter}.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * The format used to parse and format {@code java.time.LocalDateTime} objects. The string is a pattern that can be used by a
     * {@code DateTimeFormatter}.
     */
    public static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * The format used to parse and format {@code java.time.ZonedDateTime} objects. The string is a pattern that can be used by a
     * {@code DateTimeFormatter}.
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    private final Gson gson;

    /**
     * Creates a new {@code GsonObjectSerializer} with default values.
     * <p>
     * The default values include using the pattern {@value #DATE_TIME_FORMAT} when working with {@code java.time.ZonedDateTime} objects.
     */
    public GsonObjectSerializer() {
        gson = new GsonBuilder().setDateFormat(DATE_TIME_FORMAT).registerTypeAdapter(Notification.class, new NotificationDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTmeAdapter().nullSafe())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTmeAdapter().nullSafe()).create();
    }

    @Override
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <U> U deserialize(String serializedObject, Class<U> objectType) {
        if (serializedObject == null || objectType == null) {
            throw new IllegalArgumentException(
                    "Arguments must not be null: serializedObject=" + serializedObject + ", objectType=" + objectType);
        }
        try {
            return gson.fromJson(serializedObject, objectType);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("serializedObject cannot be parsed as JSON: " + serializedObject, e);
        }
    }

    private static final class NotificationDeserializer implements JsonDeserializer<Notification> {
        @Override
        public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            if (!Notification.class.getTypeName().equals(typeOfT.getTypeName())) {
                throw new IllegalStateException("Unexpected type in deserialize method, expected 'Notification'. typeOfT=" + typeOfT);
            }
            try {
                JsonObject jsonObject = json.getAsJsonObject();
                String eventType = jsonObject.get("eventType").getAsString();
                long storedEventId = jsonObject.get("storedEventId").getAsLong();
                ZonedDateTime occurredOn = context.deserialize(jsonObject.get("occurredOn"), ZonedDateTime.class);
                @SuppressWarnings("unchecked")
                Class<DomainEvent> eventClass = (Class<DomainEvent>) Class.forName(eventType);
                DomainEvent event = context.deserialize(jsonObject.get("event"), eventClass);
                ErrorHandling.checkNull("JSON notification is missing event: json=" + json, event);
                return new Notification(eventType, storedEventId, occurredOn, event);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Internal error: " + e.toString(), e);
            }
        }
    }

    private static final class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
            jsonWriter.value(localDate.format(DATE_FORMATTER));
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            return LocalDate.parse(jsonReader.nextString(), DATE_FORMATTER);
        }
    }

    private static final class LocalDateTmeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(LOCAL_DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), LOCAL_DATE_TIME_FORMATTER);
        }
    }

    private static final class ZonedDateTmeAdapter extends TypeAdapter<ZonedDateTime> {
        @Override
        public void write(final JsonWriter jsonWriter, final ZonedDateTime zonedDateTime) throws IOException {
            jsonWriter.value(zonedDateTime.format(DATE_TIME_FORMATTER));
        }

        @Override
        public ZonedDateTime read(final JsonReader jsonReader) throws IOException {
            return ZonedDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
        }
    }
}
