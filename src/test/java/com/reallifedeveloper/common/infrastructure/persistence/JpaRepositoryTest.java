package com.reallifedeveloper.common.infrastructure.persistence;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.jpa.repository.Query;

public class JpaRepositoryTest {

    private JpaTestRepository repository = new JpaTestRepository();

    @Test
    public void foo() {
        Assert.assertEquals("Wrong query string: ", "foo", repository.getQueryString("foo"));
    }

    @Test
    public void bar() {
        Assert.assertEquals("Wrong query string: ", "bar", repository.getQueryString("bar", int.class));
    }

    @Test
    public void baz() {
        Assert.assertEquals("Wrong query string: ", "baz", repository.getQueryString("baz", String.class, int.class));
    }

    @Test
    public void interfaceMethodWithoutQueryString() {
        Assert.assertNull("Query string should be null",
                repository.getQueryString("interfaceMethodWithoutQueryString"));
    }

    @Test
    public void methodWithoutQueryString() {
        Assert.assertNull("Query string should be null", repository.getQueryString("methodWithoutQueryString"));
    }

    @Test
    public void unknownMethod() {
        try {
            repository.getQueryString("noSuchMethod");
            Assert.fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            Assert.assertEquals("Wrong root cause: ", NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    private interface TestRepository {

        void foo();

        void bar(int i);

        @Query("baz")
        void baz(String s, int i);

        void interfaceMethodWithoutQueryString();
    }

    private static class JpaTestRepository extends AbstractJpaRepository implements TestRepository {

        @Override
        @Query("foo")
        public void foo() {
        }

        @Override
        @Query("bar")
        public void bar(int i) {
        }

        @Override
        public void baz(String s, int i) {
        }

        @Override
        public void interfaceMethodWithoutQueryString() {
        }

        @SuppressWarnings("unused")
        public void methodWithoutQueryString() {
        }

    }
}
