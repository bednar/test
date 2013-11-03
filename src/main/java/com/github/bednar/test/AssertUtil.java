package com.github.bednar.test;

import javax.annotation.Nonnull;

import org.junit.Assert;

/**
 * @author Jakub Bednář (03/11/2013 15:46)
 */
public final class AssertUtil
{
    /**
     * Assert that {@code throwable} is caused by exception of {@code type}.
     *
     * @param type      required type of exception
     * @param throwable cause
     */
    public static void assertException(@Nonnull final Class<? extends Exception> type, @Nonnull final Throwable throwable)
    {
        if (type.isAssignableFrom(throwable.getClass()))
        {
            return;
        }

        Throwable cause = throwable.getCause();
        if (cause == null)
        {
            Assert.fail();
        }

        assertException(type, cause);
    }
}
