package com.reallifedeveloper.common.infrastructure.persistence;

import java.util.Optional;

import javax.sql.DataSource;

import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.reallifedeveloper.common.application.notification.PublishedMessageTracker;
import com.reallifedeveloper.common.application.notification.PublishedMessageTrackerRepository;
import com.reallifedeveloper.tools.test.database.dbunit.AbstractDbTest;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring-context-rld-common-test.xml" })
public class JpaPublishedMessageTrackerRepositoryIT extends AbstractDbTest {

    @Autowired
    private PublishedMessageTrackerRepository repository;

    @Autowired
    private DataSource ds;

    @Autowired
    private IDataTypeFactory dataTypeFactory;

    public JpaPublishedMessageTrackerRepositoryIT() {
        super(null, "/dbunit/rld-common.dtd", "/dbunit/message_tracker.xml");
    }

    @Test
    public void findByPublicationChannel() {
        verifyMessageTracker("foo", 1, 0);
        verifyMessageTracker("bar", 2, 42);
    }

    @Test
    public void findByPublicationChannelNonExistingChannel() {
        Assertions.assertFalse(repository.findByPublicationChannel("baz").isPresent(), "Channel 'baz' should not exist");
    }

    @Test
    public void save() {
        PublishedMessageTracker messageTracker = repository.findByPublicationChannel("foo").get();
        messageTracker.setLastPublishedMessageid(4711);
        repository.save(messageTracker);
        verifyMessageTracker("foo", 1, 4711);
    }

    private void verifyMessageTracker(String publicationChannel, long messageTrackerId, long lastPublishedMessageId) {
        PublishedMessageTracker messageTracker = repository.findByPublicationChannel(publicationChannel).get();
        Assertions.assertNotNull(messageTracker, () -> "Message tracker for channel '" + publicationChannel + "' should be found");
        Assertions.assertEquals(messageTrackerId, messageTracker.id().get().longValue(), "Wrong message tracker ID");
        Assertions.assertEquals(lastPublishedMessageId, messageTracker.lastPublishedMessageId().longValue(),
                "Wrong last published message ID");
        Assertions.assertEquals(publicationChannel, messageTracker.publicationChannel(), "Wrong publication channel");
    }

    @Override
    protected DataSource getDataSource() {
        return ds;
    }

    @Override
    protected Optional<IDataTypeFactory> getDataTypeFactory() {
        return Optional.of(dataTypeFactory);
    }
}
