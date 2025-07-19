package com.reallifedeveloper.common.domain;

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.experimental.UtilityClass;

/**
 * Utility class for simplifying error handling.
 *
 * @author RealLifeDeveloper
 */
@UtilityClass
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public class ErrorHandling {

    /**
     * Fails if {@code arg1} is {@code null}, throwing an {@code IllegalArgumentException} with a formatted message based on
     * {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1}
     * @param arg1            the argument to check for nullness
     *
     * @throws IllegalArgumentException if {@code arg1} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2" })
    @SuppressWarnings("nullness")
    public static void checkNull(String messageTemplate, @Nullable Object arg1) {
        checkNullInternal(messageTemplate, arg1);
    }

    /**
     * Fails if {@code arg1} or {@code arg2} is {@code null}, throwing an {@code IllegalArgumentException} with a formatted message based on
     * {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1} and {@code arg2}
     * @param arg1            the first argument to check for nullness
     * @param arg2            the second argument to check for nullness
     *
     * @throws IllegalArgumentException if {@code arg1} or {@code arg2} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2", "#3" })
    @SuppressWarnings("nullness")
    public static void checkNull(String messageTemplate, @Nullable Object arg1, @Nullable Object arg2) {
        checkNullInternal(messageTemplate, arg1, arg2);
    }

    /**
     * Fails if {@code arg1}, {@code arg2} or {@code arg3} is {@code null}, throwing an {@code IllegalArgumentException} with a formatted
     * message based on {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1}, {@code arg2} and
     *                        {@code arg3}
     * @param arg1            the first argument to check for nullness
     * @param arg2            the second argument to check for nullness
     * @param arg3            the third argument to check for nullness
     *
     * @throws IllegalArgumentException if {@code arg1}, {@code arg2} or {@code arg3} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2", "#3", "#4" })
    @SuppressWarnings("nullness")
    public static void checkNull(String messageTemplate, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) {
        checkNullInternal(messageTemplate, arg1, arg2, arg3);
    }

    /**
     * Fails if {@code arg1}, {@code arg2}, {@code arg3} or {@code arg4} is {@code null}, throwing an {@code IllegalArgumentException} with
     * a formatted message based on {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1}, {@code arg2}, {@code arg3}
     *                        and {@code arg4}
     * @param arg1            the first argument to check for nullness
     * @param arg2            the second argument to check for nullness
     * @param arg3            the third argument to check for nullness
     * @param arg4            the third argument to check for nullness
     *
     * @throws IllegalArgumentException if {@code arg1}, {@code arg2}, {@code arg3} or {@code arg4} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2", "#3", "#4", "#5" })
    @SuppressWarnings({ "nullness" })
    public static void checkNull(String messageTemplate, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3,
            @Nullable Object arg4) {
        checkNullInternal(messageTemplate, arg1, arg2, arg3, arg4);
    }

    /**
     * Fails if any of {@code arg1} through {@code arg5} is {@code null}, throwing an {@code IllegalArgumentException} with a formatted
     * message based on {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1} through {@code arg7}.
     * @param arg1            the first argument to check for nullness
     * @param arg2            the second argument to check for nullness
     * @param arg3            the third argument to check for nullness
     * @param arg4            the fourth argument to check for nullness
     * @param arg5            the fifth argument to check for nullness
     *
     * @throws IllegalArgumentException if any of {@code arg1} throuch {@code arg5} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2", "#3", "#4", "#5", "#6" })
    @SuppressWarnings({ "nullness" })
    public static void checkNull(String messageTemplate, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3,
            @Nullable Object arg4, @Nullable Object arg5) {
        checkNullInternal(messageTemplate, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Fails if any of {@code arg1} through {@code arg6} is {@code null}, throwing an {@code IllegalArgumentException} with a formatted
     * message based on {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1} through {@code arg7}.
     * @param arg1            the first argument to check for nullness
     * @param arg2            the second argument to check for nullness
     * @param arg3            the third argument to check for nullness
     * @param arg4            the fourth argument to check for nullness
     * @param arg5            the fifth argument to check for nullness
     * @param arg6            the sixth argument to check for nullness
     *
     * @throws IllegalArgumentException if any of {@code arg1} throuch {@code arg7} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2", "#3", "#4", "#5", "#6", "#7" })
    @SuppressWarnings({ "nullness" })
    public static void checkNull(String messageTemplate, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3,
            @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6) {
        checkNullInternal(messageTemplate, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /**
     * Fails if any of {@code arg1} through {@code arg7} is {@code null}, throwing an {@code IllegalArgumentException} with a formatted
     * message based on {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1} through {@code arg7}.
     * @param arg1            the first argument to check for nullness
     * @param arg2            the second argument to check for nullness
     * @param arg3            the third argument to check for nullness
     * @param arg4            the fourth argument to check for nullness
     * @param arg5            the fifth argument to check for nullness
     * @param arg6            the sixth argument to check for nullness
     * @param arg7            the seventh argument to check for nullness
     *
     * @throws IllegalArgumentException if any of {@code arg1} throuch {@code arg7} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2", "#3", "#4", "#5", "#6", "#7", "#8" })
    @SuppressWarnings({ "nullness" })
    public static void checkNull(String messageTemplate, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3,
            @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7) {
        checkNullInternal(messageTemplate, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    /**
     * Fails if any of {@code arg1} through {@code arg8} is {@code null}, throwing an {@code IllegalArgumentException} with a formatted
     * message based on {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code arg1} through {@code arg7}.
     * @param arg1            the first argument to check for nullness
     * @param arg2            the second argument to check for nullness
     * @param arg3            the third argument to check for nullness
     * @param arg4            the fourth argument to check for nullness
     * @param arg5            the fifth argument to check for nullness
     * @param arg6            the sixth argument to check for nullness
     * @param arg7            the seventh argument to check for nullness
     * @param arg8            the eigth argument to check for nullness
     *
     * @throws IllegalArgumentException if any of {@code arg1} throuch {@code arg7} is {@code null}
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2", "#3", "#4", "#5", "#6", "#7", "#8", "#9" })
    @SuppressWarnings({ "nullness" })
    public static void checkNull(String messageTemplate, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3,
            @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8) {
        checkNullInternal(messageTemplate, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }

    @SuppressFBWarnings(value = "FORMAT_STRING_MANIPULATION", justification = "The format string is provided by the programmer, not user")
    private static void checkNullInternal(String messageTemplate, @Nullable Object... arguments) {
        // We know that arguments will never be null, it will always be an array, since
        // this method is only called using varargs
        if (messageTemplate == null) {
            throw new IllegalStateException(
                    String.format("checkNull called incorrectly: messageTemplate=null, arguments=%s", Arrays.asList(arguments)));
        }
        for (Object argument : arguments) {
            if (argument == null) {
                String errorMessage = String.format(messageTemplate, arguments);
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    /**
     * Fails if the string {@code s} is {@code null} or blank, throwing an {@code IllegalArgumentException} with a formatted message based
     * on {@code messageTemplate}.
     *
     * @param messageTemplate a {@code String.format()} format string that will be formatted using {@code s}
     * @param s               the string to check for nullness or blankness
     *
     * @throws IllegalArgumentException if {@code s} is {@code null} or blank, i.e., empty or only containing whitespace
     * @throws IllegalStateException    if {@code messageTemplate} is {@code null}
     */
    @EnsuresNonNull({ "#2" })
    @SuppressFBWarnings(value = "FORMAT_STRING_MANIPULATION", justification = "The format string is provided by the programmer, not user")
    public static void checkNullOrBlank(String messageTemplate, @Nullable String s) {
        if (messageTemplate == null) {
            throw new IllegalStateException(String.format("checkNullOrBlank called incorrectly: messageTemplate=null, s='%s'", s));
        }
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(String.format(messageTemplate, s));
        }
    }
}
