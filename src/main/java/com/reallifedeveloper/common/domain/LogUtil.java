package com.reallifedeveloper.common.domain;

import lombok.experimental.UtilityClass;

/**
 * A utility class to simplify working with logs.
 *
 * @author RealLifeDeveloper
 */
@UtilityClass
public class LogUtil {

    /**
     * Removes all occurrences of carriage return ({@code \r}) and linefeed ({@code \n}) from a string.
     * <p>
     * This is useful when logging something that originates from a user, in order to avoid CRLF injection. See
     * <a href="https://www.invicti.com/learn/crlf-injection/">https://www.invicti.com/learn/crlf-injection/</a>.
     * <p>
     * The method is null-safe and returns {@code null} if the input string is {@code null}.
     *
     * @param s the string from which to remove CRLF
     *
     * @return {@code s} with all occurrences of CR and LF removed
     */
    @SuppressWarnings({ "checkstyle:noReturnNull" })
    public static String removeCRLF(String s) {
        if (s == null) {
            return null;
        } else {
            return s.replaceAll("[\r\n]", "");
        }
    }

    /**
     * Given a non-null object, calls the {@code toString} method on the object and returns the result of calling
     * {@link #removeCRLF(String)} on the string representation of the object.
     * <p>
     * For {@code null} simply returns {@code null}.
     *
     * @param o the object for which to remove CRLF from its string representation
     *
     * @return {@code o.toString()} with all occurrences of CR and LF removed, or {@code null} if {@code o} is {@code null}
     */
    @SuppressWarnings({ "checkstyle:noReturnNull" })
    public static String removeCRLF(Object o) {
        if (o == null) {
            return null;
        } else {
            return removeCRLF(o.toString());
        }
    }
}
