package com.reallifedeveloper.common.application.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.infrastructure.persistence.BaseJpaEntity;

/**
 * Keeps track of the most recently published message on a certain publication channel.
 * <p>
 * The publication channel can, for example, be an exchange in a messaging system.
 *
 * @author RealLifeDeveloper
 */
@Entity
@Table(name = "message_tracker")
public class PublishedMessageTracker extends BaseJpaEntity<Long> {

    @Column(name = "last_published_message_id", nullable = false, unique = false)
    private Long lastPublishedMessageId;

    @Column(name = "publication_channel", nullable = false, unique = true)
    private String publicationChannel;

    /**
     * Creates a new {@code PublishedMessageTracker} with the id of the most recently published message on the given publication channel.
     *
     * @param lastPublishedMessageId the id of the most recently published message on the publication channel
     * @param publicationChannel     the name of the publication channel, e.g., the name of an exchange in a messaging system
     */
    public PublishedMessageTracker(long lastPublishedMessageId, String publicationChannel) {
        ErrorHandling.checkNull("publicationChannel must not be null", publicationChannel);
        this.lastPublishedMessageId = lastPublishedMessageId;
        this.publicationChannel = publicationChannel;
    }

    /* package-private */
    /**
     * Required by JPA.
     */
    @SuppressWarnings("NullAway")
    PublishedMessageTracker() {
        super();
    }

    /**
     * Gives the id of the most recently published message on the publication channel associated with this {@code PublishedMessageTracker}.
     *
     * @return the id of the most recently published message
     */
    public Long lastPublishedMessageId() {
        return lastPublishedMessageId;
    }

    /**
     * Sets the id of the most recently published message on the publication channel associated with this {@code PublishedMessageTracker}.
     *
     * @param newLastPublishedMessageId the new id of the most recently published message
     */
    public void setLastPublishedMessageid(long newLastPublishedMessageId) {
        this.lastPublishedMessageId = newLastPublishedMessageId;
    }

    /**
     * Gives the name of the publication channel for this {@code PublishedMessageTracker}.
     *
     * @return the name of the publication channel
     */
    public String publicationChannel() {
        return publicationChannel;
    }

}
