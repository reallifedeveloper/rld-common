package com.reallifedeveloper.common.infrastructure.persistence;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.reallifedeveloper.common.domain.TestTimeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring-context-rld-common-registry-test.xml" })
public class TimestampedJpaEntityTest {

    @Autowired
    private TestTimeService timeService;

    private Date testDate = new Date();

    @Before
    public void init() {
        timeService.setDates(testDate);
    }

    @Test
    public void constructor() {
        TimestampedJpaEntity entity = new TimestampedJpaEntity();
        Assert.assertNull("ID should be null", entity.id());
        Assert.assertNull("Created date should be null", entity.created());
        Assert.assertNull("Updated date should be null", entity.updated());
    }

    @Test
    public void constructorId() {
        long id = 42;
        TimestampedJpaEntity entity = new TimestampedJpaEntity(id);
        Assert.assertEquals("Wrong ID: ", id, entity.id().longValue());
        Assert.assertEquals("Wrong created date: ", testDate, entity.created());
        Assert.assertEquals("Wrong updated date: ", testDate, entity.updated());
    }

    @Test
    public void constructorIdCreatedUpdated() {
        long id = 42;
        Date created = new Date();
        Date updated = new Date(created.getTime() + 1);
        TimestampedJpaEntity entity = new TimestampedJpaEntity(id, created, updated);
        Assert.assertEquals("Wrong ID: ", id, entity.id().longValue());
        Assert.assertEquals("Wrong created date: ", created, entity.created());
        Assert.assertEquals("Wrong updated date: ", updated, entity.updated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorCreatedNull() {
        long id = 42;
        Date updated = new Date();
        new TimestampedJpaEntity(id, null, updated);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorUpdatedNull() {
        long id = 42;
        Date created = new Date();
        new TimestampedJpaEntity(id, created, null);
    }

    @Test
    public void setUpdated() {
        Date updated = new Date();
        TimestampedJpaEntity entity = new TimestampedJpaEntity();
        Assert.assertNull("Updated date should be null", entity.updated());
        entity.setUpdated(updated);
        Assert.assertEquals("Wrong updated date: ", updated, entity.updated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUpdatedNull() {
        TimestampedJpaEntity entity = new TimestampedJpaEntity();
        entity.setUpdated(null);
    }

    private static class TimestampedJpaEntity extends AbstractTimestampedJpaEntity<Long> {

        TimestampedJpaEntity() {
            super();
        }

        TimestampedJpaEntity(Long id) {
            super(id);
        }

        TimestampedJpaEntity(Long id, Date created, Date updated) {
            super(id, created, updated);
        }
    }
}
