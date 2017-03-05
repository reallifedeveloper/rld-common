package com.reallifedeveloper.common.infrastructure.persistence;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract base class for all JPA entities.
 *
 * @author RealLifeDeveloper
 *
 * @param <ID> the type of the primary key
 */
@MappedSuperclass
public abstract class AbstractJpaEntity<ID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final ID id;

    /**
     * Creates a new <code>AbstractJpaEntity</code> with null ID.
     */
    protected AbstractJpaEntity() {
        this(null);
    }

    /**
     * Creates a new <code>AbstractJpaEntity</code> with the given ID.
     *
     * @param id the ID of the new entity, may be <code>null</code>
     */
    protected AbstractJpaEntity(ID id) {
        this.id = id;
    }

    /**
     * Gives the ID of this <code>AbstractJpaEntity</code>.
     *
     * @return the ID of this entity
     */
    public ID id() {
        return id;
    }

}
