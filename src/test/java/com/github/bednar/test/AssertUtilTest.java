package com.github.bednar.test;

import javax.annotation.Nonnull;

import org.junit.Test;

/**
 * @author Jakub Bednář (26/11/2013 16:41)
 */
public class AssertUtilTest
{
    @Test
    public void deepAssert()
    {
        AssertUtil.assertException(IllegalAccessException.class, createThrowable());
    }

    @Test (expected = AssertionError.class)
    public void deepNotAssert()
    {
        AssertUtil.assertException(NullPointerException.class, createThrowable());
    }

    @Nonnull
    private Throwable createThrowable()
    {
        return new RuntimeException(new IllegalAccessException());
    }
}
