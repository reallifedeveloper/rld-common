package com.reallifedeveloper.common.infrastructure.persistence;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistry;

/**
 * A base class for JPA entities that need to keep track of when they where created and when they were last updated.
 *
 * @author RealLifeDeveloper
 *
 * @param <ID> the type of the primary key
 */
@MappedSuperclass
public class TimestampedJpaEntity<ID> extends BaseJpaEntity<ID> {

    @Column(name = "created", nullable = false)
    private @Nullable ZonedDateTime created;

    @Column(name = "updated", nullable = false)
    private @Nullable ZonedDateTime updated;

    /**
     * Creates a new {@code AbstractTimestampedJpaEntity} with null ID and with the created and updated timestamps also set to null.
     * <p>
     * This constructor should normally only be used by Hibernate, and you are encouraged to create a no-argument package private
     * constructor in your entities that call this.
     */
    protected TimestampedJpaEntity() {
        super();
    }

    /**
     * Creates a new {@code AbstractTimestampedJpaEntity} with the given ID and with the created and updated timestamps set to the current
     * time.
     *
     * @param id the ID of the new entity
     */
    protected TimestampedJpaEntity(ID id) {
        this(id, CommonDomainRegistry.timeService().now(), CommonDomainRegistry.timeService().now());
    }

    /**
     * Creates a new {@code AbstractTimestampedJpaEntity} with the given ID and timestamps.
     *
     * @param id      the ID of the new entity
     * @param created the time and date the entity was created
     * @param updated the time and date the entity was last updated
     *
     * @throws IllegalArgumentException if {@code created} or {@code updated} is {@code null}
     */
    protected TimestampedJpaEntity(ID id, ZonedDateTime created, ZonedDateTime updated) {
        super(id);
        ErrorHandling.checkNull("Arguments must not be null: id=" + id + ", created=%s, updated=%s", created, updated);
        this.created = created;
        this.updated = updated;
    }

    /**
     * Gives the time and date that the entity was created.
     *
     * @return the timestamp of creation
     */
    public Optional<ZonedDateTime> created() {
        return Optional.ofNullable(created);
    }

    /**
     * Gives the time and date that the entity was last updated.
     *
     * @return the timestamp of last update
     */
    public Optional<ZonedDateTime> updated() {
        return Optional.ofNullable(updated);
    }

    /**
     * Sets the time and date that the entity was last updated.
     *
     * @param updated the timestamp of last update
     * @throws IllegalArgumentException if {@code updated} is {@code null}
     */
    public void setUpdated(ZonedDateTime updated) {
        ErrorHandling.checkNull("updated must not be null", updated);
        this.updated = updated;
    }
}
