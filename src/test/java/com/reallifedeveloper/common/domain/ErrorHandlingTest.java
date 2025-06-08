package com.reallifedeveloper.common.domain;

import static com.reallifedeveloper.common.domain.ErrorHandling.checkNull;
import static com.reallifedeveloper.common.domain.ErrorHandling.checkNullOrBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorHandlingTest {

    @Test
    public void checkNullSingleArgumentIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> checkNull("Arguments must not be null: foo=%s", null));
        assertEquals("Arguments must not be null: foo=null", e.getMessage());
    }

    @Test
    public void checkNullSingleArgumentIsNullWithMessageTemplateThatDoesNotUseArgument() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> checkNull("foo must not be null", null));
        assertEquals("foo must not be null", e.getMessage());
    }

    @Test
    public void checkNullSingleArgumentIsNonNull() {
        // Check that no exception is thrown:
        checkNull("Arguments must not be null: foo=%s", "foo");
    }

    @Test
    public void checkNullTwoArgumentsFirstIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> checkNull("Arguments must not be null: foo=%s, bar=%s", null, "bar"));
        assertEquals("Arguments must not be null: foo=null, bar=bar", e.getMessage());
    }

    @Test
    public void checkNullTwoArgumentsSecondIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> checkNull("Arguments must not be null: foo=%s, bar=%s", "foo", null));
        assertEquals("Arguments must not be null: foo=foo, bar=null", e.getMessage());
    }

    @Test
    public void checkNullTwoArgumentsBothAreNonNull() {
        // Check that no exception is thrown:
        checkNull("Arguments must not be null: foo=%s", "foo", "bar");
    }

    @Test
    public void checkNullThreeArgumentsFirstIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> checkNull("Arguments must not be null: foo=%s, bar=%s, baz=%s", null, "bar", "baz"));
        assertEquals("Arguments must not be null: foo=null, bar=bar, baz=baz", e.getMessage());
    }

    @Test
    public void checkNullThreeArgumentsSecondIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> checkNull("Arguments must not be null: foo=%s, bar=%s, baz=%s", "foo", null, "baz"));
        assertEquals("Arguments must not be null: foo=foo, bar=null, baz=baz", e.getMessage());
    }

    @Test
    public void checkNullThreeArgumentsThirdIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> checkNull("Arguments must not be null: foo=%s, bar=%s, baz=%s", "foo", "bar", null));
        assertEquals("Arguments must not be null: foo=foo, bar=bar, baz=null", e.getMessage());
    }

    @Test
    public void checkNullThreeArgumentsAllAreNonNull() {
        Assertions.assertDoesNotThrow(() -> {
            checkNull("Arguments must not be null: foo=%s", "foo", "bar", "baz");
        });
    }

    @Test
    public void checkNullFourArgumentsAllAreNonNull() {
        Assertions.assertDoesNotThrow(() -> {
            checkNull("Arguments must not be null: foo=%s", "foo", "bar", "baz", "hoo");
        });
    }

    @Test
    public void checkNullArgumentsFourthIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> checkNull("Arguments must not be null: foo=%s, bar=%s, baz=%s, hoo=%s", "foo", "bar", "baz", null));
        assertEquals("Arguments must not be null: foo=foo, bar=bar, baz=baz, hoo=null", e.getMessage());
    }

    @Test
    public void checkNullWithNullMessageTemplate() {
        Exception e = assertThrows(IllegalStateException.class, () -> checkNull(null, "foo", "bar", "baz"));
        assertEquals("checkNull called incorrectly: messageTemplate=null, arguments=[foo, bar, baz]", e.getMessage());
    }

    @Test
    public void checkNullWithNullArguments() {
        // This test is to show that there is no way to call checkNullInternal with a null array, it will always be an array
        // that may contain null.
        Exception e = assertThrows(IllegalArgumentException.class, () -> checkNull("foo: %s", (Object[]) null));
        assertEquals("foo: null", e.getMessage());
    }

    @Test
    public void checkNullOrBlankWithNullString() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> checkNullOrBlank("foo must not be null or blank: foo='%s'", null));
        assertEquals("foo must not be null or blank: foo='null'", e.getMessage());
    }

    @Test
    public void checkNullOrBlankWithBlankString() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> checkNullOrBlank("foo must not be null or blank: foo='%s'", "  "));
        assertEquals("foo must not be null or blank: foo='  '", e.getMessage());
    }

    @Test
    public void checkNullOrBlankWithNonBlankString() {
        // Check that no exception is thrown:
        checkNullOrBlank("foo must not be null or blank: foo='%s'", "foo");
    }

    @Test
    public void checkNullOrBlankWithNullMessageTemplate() {
        Exception e = assertThrows(IllegalStateException.class, () -> checkNullOrBlank(null, "foo"));
        assertEquals("checkNullOrBlank called incorrectly: messageTemplate=null, s='foo'", e.getMessage());
    }
}
