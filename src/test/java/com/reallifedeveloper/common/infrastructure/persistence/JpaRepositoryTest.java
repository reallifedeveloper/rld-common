package com.reallifedeveloper.common.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

public class JpaRepositoryTest {

    private final JpaTestRepository repository = new JpaTestRepository();

    @Test
    public void foo() {
        assertEquals("foo", repository.getQueryString("foo").get(), "Wrong query string");
    }

    @Test
    public void bar() {
        assertEquals("bar", repository.getQueryString("bar", int.class).get(), "Wrong query string");
    }

    @Test
    public void baz() {
        assertEquals("baz", repository.getQueryString("baz", String.class, int.class).get(), "Wrong query string");
    }

    @Test
    public void interfaceMethodWithoutQueryString() {
        assertFalse(repository.getQueryString("interfaceMethodWithoutQueryString").isPresent(), "Query string should not be present");
    }

    @Test
    public void methodWithoutQueryString() {
        assertFalse(repository.getQueryString("methodWithoutQueryString").isPresent(), "Query string should not be present");
    }

    @Test
    public void unknownMethod() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> repository.getQueryString("noSuchMethod"));
        assertNotNull(thrown.getCause(), "Expected root cause to be present in exception: " + thrown);
        assertEquals(NoSuchMethodException.class, thrown.getCause().getClass(), "Wrong root cause");
    }

    @SuppressWarnings("unused")
    private interface TestRepository {

        void foo();

        void bar(int i);

        @Query("baz")
        void baz(String s, int i);

        void interfaceMethodWithoutQueryString();
    }

    private static class JpaTestRepository extends BaseJpaRepository implements TestRepository {

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

        @SuppressWarnings("unused")
        void methodWithoutQueryString() {
        }

        @Override
        public void interfaceMethodWithoutQueryString() {
        }
    }
}
