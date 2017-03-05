package com.reallifedeveloper.common.domain;

/**
 * A domain-driven design aggregate root.
 * <p>
 * An aggregate is a collection of objects that are bound together by a root entity,
 * the <em>aggregate root</em>. The aggregate root guarantees the consistency of
 * changes being made within the aggregate by forbidding external objects from
 * holding references to its members.
 *
 * @author RealLifeDeveloper
 *
 * @param <T> the type of the aggregate root, a domain entity
 */
public interface AggregateRoot<T extends DomainEntity<T, ?>> {

}
