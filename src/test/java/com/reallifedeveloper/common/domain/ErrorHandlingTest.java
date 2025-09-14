package com.reallifedeveloper.common.domain;

import static com.reallifedeveloper.common.domain.ErrorHandling.checkNull;
import static com.reallifedeveloper.common.domain.ErrorHandling.checkNullOrBlank;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@SuppressWarnings("NullAway")
public class ErrorHandlingTest {

    @Test
    public void checkNullOneArgument() {
        String mssageTemplate = "arg1 must not be null";
        testCheckNull(mssageTemplate, "arg1 must not be null", (Object) null);
        testCheckNull(mssageTemplate, null, "arg1");
    }

    @Test
    public void checkNullTwoArguments() {
        String mssageTemplate = "Arguments must not be null: arg1=%s, arg2=%s";
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=null, arg2=arg2", null, "arg2");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=null", "arg1", null);
        testCheckNull(mssageTemplate, null, "arg1", "arg2");
    }

    @Test
    public void checkNullThreeArguments() {
        String mssageTemplate = "Arguments must not be null: arg1=%s, arg2=%s, arg3=%s";
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=null, arg2=arg2, arg3=arg3", null, "arg2", "arg3");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=null, arg3=arg3", "arg1", null, "arg3");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=null", "arg1", "arg2", null);
        testCheckNull(mssageTemplate, null, "arg1", "arg2", "arg3");
    }

    @Test
    public void checkNullFourArguments() {
        String mssageTemplate = "Arguments must not be null: arg1=%s, arg2=%s, arg3=%s, arg4=%s";
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=null, arg2=arg2, arg3=arg3, arg4=arg4", null, "arg2", "arg3",
                "arg4");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=null, arg3=arg3, arg4=arg4", "arg1", null, "arg3",
                "arg4");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=null, arg4=arg4", "arg1", "arg2", null,
                "arg4");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=null", "arg1", "arg2", "arg3",
                null);
        testCheckNull(mssageTemplate, null, "arg1", "arg2", "arg3", "arg4");
    }

    @Test
    public void checkNullFiveArguments() {
        String mssageTemplate = "Arguments must not be null: arg1=%s, arg2=%s, arg3=%s, arg4=%s, arg5=%s";
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=null, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5", null, "arg2",
                "arg3", "arg4", "arg5");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=null, arg3=arg3, arg4=arg4, arg5=arg5", "arg1", null,
                "arg3", "arg4", "arg5");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=null, arg4=arg4, arg5=arg5", "arg1", "arg2",
                null, "arg4", "arg5");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=null, arg5=arg5", "arg1", "arg2",
                "arg3", null, "arg5");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=null", "arg1", "arg2",
                "arg3", "arg4", null);
        testCheckNull(mssageTemplate, null, "arg1", "arg2", "arg3", "arg4", "arg5");
    }

    @Test
    public void checkNullSixArguments() {
        String mssageTemplate = "Arguments must not be null: arg1=%s, arg2=%s, arg3=%s, arg4=%s, arg5=%s, arg6=%s";
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=null, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6", null,
                "arg2", "arg3", "arg4", "arg5", "arg6");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=null, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6",
                "arg1", null, "arg3", "arg4", "arg5", "arg6");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=null, arg4=arg4, arg5=arg5, arg6=arg6",
                "arg1", "arg2", null, "arg4", "arg5", "arg6");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=null, arg5=arg5, arg6=arg6",
                "arg1", "arg2", "arg3", null, "arg5", "arg6");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=null, arg6=arg6",
                "arg1", "arg2", "arg3", "arg4", null, "arg6");
        testCheckNull(mssageTemplate, "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=null",
                "arg1", "arg2", "arg3", "arg4", "arg5", null);
        testCheckNull(mssageTemplate, null, "arg1", "arg2", "arg3", "arg4", "arg5", "arg6");
    }

    @Test
    public void checkNullSevenArguments() {
        String mssageTemplate = "Arguments must not be null: arg1=%s, arg2=%s, arg3=%s, arg4=%s, arg5=%s, arg6=%s, arg7=%s";
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=null, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6, arg7=arg7", null, "arg2",
                "arg3", "arg4", "arg5", "arg6", "arg7");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=null, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6, arg7=arg7", "arg1", null,
                "arg3", "arg4", "arg5", "arg6", "arg7");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=null, arg4=arg4, arg5=arg5, arg6=arg6, arg7=arg7", "arg1", "arg2",
                null, "arg4", "arg5", "arg6", "arg7");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=null, arg5=arg5, arg6=arg6, arg7=arg7", "arg1", "arg2",
                "arg3", null, "arg5", "arg6", "arg7");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=null, arg6=arg6, arg7=arg7", "arg1", "arg2",
                "arg3", "arg4", null, "arg6", "arg7");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=null, arg7=arg7", "arg1", "arg2",
                "arg3", "arg4", "arg5", null, "arg7");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6, arg7=null", "arg1", "arg2",
                "arg3", "arg4", "arg5", "arg6", null);
        testCheckNull(mssageTemplate, null, "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7");
    }

    @Test
    public void checkNullEightArguments() {
        String mssageTemplate = "Arguments must not be null: arg1=%s, arg2=%s, arg3=%s, arg4=%s, arg5=%s, arg6=%s, arg7=%s, arg8=%s";
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=null, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6, arg7=arg7, arg8=arg8", null,
                "arg2", "arg3", "arg4", "arg5", "arg6", "arg7", "arg8");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=null, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6, arg7=arg7, arg8=arg8",
                "arg1", null, "arg3", "arg4", "arg5", "arg6", "arg7", "arg8");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=null, arg4=arg4, arg5=arg5, arg6=arg6, arg7=arg7, arg8=arg8",
                "arg1", "arg2", null, "arg4", "arg5", "arg6", "arg7", "arg8");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=null, arg5=arg5, arg6=arg6, arg7=arg7, arg8=arg8",
                "arg1", "arg2", "arg3", null, "arg5", "arg6", "arg7", "arg8");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=null, arg6=arg6, arg7=arg7, arg8=arg8",
                "arg1", "arg2", "arg3", "arg4", null, "arg6", "arg7", "arg8");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=null, arg7=arg7, arg8=arg8",
                "arg1", "arg2", "arg3", "arg4", "arg5", null, "arg7", "arg8");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6, arg7=null, arg8=arg8",
                "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", null, "arg8");
        testCheckNull(mssageTemplate,
                "Arguments must not be null: arg1=arg1, arg2=arg2, arg3=arg3, arg4=arg4, arg5=arg5, arg6=arg6, arg7=arg7, arg8=null",
                "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7", null);
        testCheckNull(mssageTemplate, null, "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7", "arg8");
    }

    private static void testCheckNull(String messageTemplate, String expectedMessage, Object... args) {
        Executable checkNullCall = switch (args.length) {
        case 1 -> {
            yield () -> checkNull(messageTemplate, args[0]);
        }
        case 2 -> {
            yield () -> checkNull(messageTemplate, args[0], args[1]);
        }
        case 3 -> {
            yield () -> checkNull(messageTemplate, args[0], args[1], args[2]);
        }
        case 4 -> {
            yield () -> checkNull(messageTemplate, args[0], args[1], args[2], args[3]);
        }
        case 5 -> {
            yield () -> checkNull(messageTemplate, args[0], args[1], args[2], args[3], args[4]);
        }
        case 6 -> {
            yield () -> checkNull(messageTemplate, args[0], args[1], args[2], args[3], args[4], args[5]);
        }
        case 7 -> {
            yield () -> checkNull(messageTemplate, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        }
        case 8 -> {
            yield () -> checkNull(messageTemplate, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        }
        default -> throw new IllegalStateException("Unexepcted number of argumets: " + args.length);
        };

        if (expectedMessage == null) {
            assertDoesNotThrow(checkNullCall);
        } else {
            Exception expectedException = assertThrows(IllegalArgumentException.class, checkNullCall);
            assertEquals(expectedMessage, expectedException.getMessage());
        }

    }

    @Test
    public void checkNullWithNullMessageTemplate() {
        Exception e = assertThrows(IllegalStateException.class, () -> checkNull(null, "foo", "bar", "baz"));
        assertEquals("checkNull called incorrectly: messageTemplate=null, arguments=[foo, bar, baz]", e.getMessage());
    }

    @Test
    public void checkNullWithNullParameterArray() {
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
