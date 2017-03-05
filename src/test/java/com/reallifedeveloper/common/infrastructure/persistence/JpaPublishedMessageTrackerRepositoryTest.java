package com.reallifedeveloper.common.infrastructure.persistence;

import javax.sql.DataSource;

import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.reallifedeveloper.common.application.notification.PublishedMessageTracker;
import com.reallifedeveloper.common.application.notification.PublishedMessageTrackerRepository;
import com.reallifedeveloper.tools.test.database.dbunit.AbstractDbTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring-context-rld-common-test.xml" })
public class JpaPublishedMessageTrackerRepositoryTest extends AbstractDbTest {

    @Autowired
    private PublishedMessageTrackerRepository repository;

    @Autowired
    private DataSource ds;

    @Autowired
    private IDataTypeFactory dataTypeFactory;

    public JpaPublishedMessageTrackerRepositoryTest() {
        super(null, "/dbunit/rld-common.dtd", "/dbunit/message_tracker.xml");
    }

    @Test
    public void findByPublicationChannel() {
        verifyMessageTracker("foo", 1, 0);
        verifyMessageTracker("bar", 2, 42);
    }

    @Test
    public void findByPublicationChannelNonExistingChannel() {
        Assert.assertNull("Channel 'baz' should not exist", repository.findByPublicationChannel("baz"));
    }

    @Test
    public void save() {
        PublishedMessageTracker messageTracker = repository.findByPublicationChannel("foo");
        messageTracker.setLastPublishedMessageid(4711);
        repository.save(messageTracker);
        verifyMessageTracker("foo", 1, 4711);
    }

    private void verifyMessageTracker(String publicationChannel, long messageTrackerId,
            long lastPublishedMessageId) {
        PublishedMessageTracker messageTracker = repository.findByPublicationChannel(publicationChannel);
        Assert.assertNotNull("Message tracker for channel '" + publicationChannel + "' should be found",
                messageTracker);
        Assert.assertEquals("Wrong message tracker ID: ", messageTrackerId,
                messageTracker.id().longValue());
        Assert.assertEquals("Wrong last published message ID: ", lastPublishedMessageId,
                messageTracker.lastPublishedMessageId().longValue());
        Assert.assertEquals("Wrong publication channel: ", publicationChannel,
                messageTracker.publicationChannel());
    }

    @Override
    protected DataSource getDataSource() {
        return ds;
    }

    @Override
    protected IDataTypeFactory getDataTypeFactory() {
        return dataTypeFactory;
    }
}
