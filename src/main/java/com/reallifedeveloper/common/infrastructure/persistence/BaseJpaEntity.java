package com.reallifedeveloper.common.infrastructure.persistence;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Base class for all JPA entities.
 *
 * @author RealLifeDeveloper
 *
 * @param <ID> the type of the primary key
 */
@MappedSuperclass
public class BaseJpaEntity<ID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final @Nullable ID id;

    /**
     * Creates a new {@code AbstractJpaEntity} with null ID.
     */
    protected BaseJpaEntity() {
        this(null);
    }

    /**
     * Creates a new {@code AbstractJpaEntity} with the given ID.
     *
     * @param id the ID of the new entity, may be {@code null}
     */
    protected BaseJpaEntity(@Nullable ID id) {
        this.id = id;
    }

    /**
     * Gives the ID of this {@code AbstractJpaEntity}.
     *
     * @return the ID of this entity
     */
    public Optional<ID> id() {
        return Optional.ofNullable(id);
    }

    /**
     * Make finalize method final to avoid "Finalizer attacks" and corresponding SpotBugs warning (CT_CONSTRUCTOR_THROW).
     *
     * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/OBJ11-J.+Be+wary+of+letting+constructors+throw+exceptions">
     *      Explanation of finalizer attack</a>
     */
    @Override
    @Deprecated
    @SuppressWarnings({ "Finalize", "checkstyle:NoFinalizer", "PMD.EmptyFinalizer" })
    protected final void finalize() throws Throwable {
        // Do nothing
    }
}
