package com.reallifedeveloper.common.infrastructure.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.reallifedeveloper.common.domain.registry.CommonDomainRegistry;

/**
 * An abstract base class for JPA entities that need to keep track of when they where
 * created and when they were last updated.
 *
 * @author RealLifeDeveloper
 *
 * @param <ID> the type of the primary key
 */
@MappedSuperclass
public abstract class AbstractTimestampedJpaEntity<ID> extends AbstractJpaEntity<ID> {

    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    /**
     * Creates a new <code>AbstractTimestampedJpaEntity</code> with null ID
     * and with the created and updated timestamps also set to null.
     * <p>
     * This constructor should normally only be used by Hibernate, and you are
     * encouraged to create a no-argument package private constructor in your
     * entities that call this.
     */
    protected AbstractTimestampedJpaEntity() {
        super();
    }

    /**
     * Creates a new <code>AbstractTimestampedJpaEntity</code> with the given ID
     * and with the created and updated timestamps set to the current time.
     *
     * @param id the ID of the new entity
     */
    protected AbstractTimestampedJpaEntity(ID id) {
        this(id, CommonDomainRegistry.timeService().now(), CommonDomainRegistry.timeService().now());
    }

    /**
     * Creates a new <code>AbstractTimestampedJpaEntity</code> with the given ID and
     * timestamps.
     *
     * @param id the ID of the new entity
     * @param created the time and date the entity was created
     * @param updated the time and date the entity was last updated
     * @throws IllegalArgumentException if <code>created</code> or <code>update</code> is <code>null</code>
     */
    protected AbstractTimestampedJpaEntity(ID id, Date created, Date updated) {
        super(id);
        if (created == null || updated == null) {
            throw new IllegalArgumentException("Arguments must not be null: id=" + id
                    + ", created=" + created + ", updated=" + updated);
        }
        this.created = new Date(created.getTime());
        this.updated = new Date(updated.getTime());
    }

    /**
     * Gives the time and date that the entity was created.
     *
     * @return the timestamp of creation
     */
    public Date created() {
        if (created == null) {
            return null;
        }
        return new Date(created.getTime());
    }

    /**
     * Gives the time and date that the entity was last updated.
     *
     * @return the timestamp of last update
     */
    public Date updated() {
        if (updated == null) {
            return null;
        }
        return new Date(updated.getTime());
    }

    /**
     * Sets the time and date that the entity was last updated.
     *
     * @param updated the timestamp of last update
     * @throws IllegalArgumentException if <code>updated</code> is <code>null</code>
     */
    public void setUpdated(Date updated) {
        if (updated == null) {
            throw new IllegalArgumentException("updated must not be null");
        }
        this.updated = new Date(updated.getTime());
    }
}
