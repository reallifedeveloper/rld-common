package com.reallifedeveloper.common.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.reallifedeveloper.common.domain.LogUtil.removeCRLF;

import org.junit.jupiter.api.Test;

public class LogUtilTest {

    @Test
    public void removeCRLFIsNullSafe() {
        assertEquals(null, removeCRLF(null));
    }

    @Test
    public void removeCRLFRemovesSingleNewLine() {
        assertEquals("foobar", removeCRLF("foo\nbar"));
    }

    @Test
    public void removeCRLFRemovesSingleCarriageReturn() {
        assertEquals("foobar", removeCRLF("foo\rbar"));
    }

    @Test
    public void removeCRLFRemovesMultipleNewLinesAndCarriageReturns() {
        assertEquals("foobar", removeCRLF("f\no\ro\r\nb\n\ra\r\n\r\nr"));
    }

    @Test
    public void removeCRLFNullObjectDoesNotThrowException() {
        assertEquals(null, removeCRLF((Object) null));
    }

    @Test
    public void removeCRLFCallsToStringOnNonNullObject() {
        assertEquals("foobar", removeCRLF(new FooBar()));
    }

    private static final class FooBar {
        public String toString() {
            return "f\no\ro\r\nb\n\ra\r\n\r\nr";
        }
    }
}
