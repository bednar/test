package com.github.bednar.test;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Jakub Bednář (26/11/2013 16:49)
 */
public class SecurityInitTest
{
    @Test
    public void notNull()
    {
        SecurityInit init = SecurityInit.build();

        Assert.assertNotNull(init);
    }

    @Test
    public void authenticated()
    {
        SecurityInit
                .build()
                .buildSubject("admin");

        Assert.assertTrue(SecurityUtils.getSubject().isAuthenticated());
        Assert.assertEquals("admin", SecurityUtils.getSubject().getPrincipal());
    }

    @Test
    public void unAuthenticated()
    {
        SecurityInit
                .build()
                .destroySubject();

        Assert.assertFalse(SecurityUtils.getSubject().isAuthenticated());
        Assert.assertNull(SecurityUtils.getSubject().getPrincipal());
    }

    @Test
    public void bindSecurityManager()
    {
        WebSecurityManager manager = Mockito.mock(WebSecurityManager.class);

        SecurityInit
                .build()
                .bindSecurityManager(buildServletContext(manager));

        Assert.assertNotNull(ThreadContext.getSecurityManager());
        Assert.assertEquals(manager, ThreadContext.getSecurityManager());

        Assert.assertNotNull(SecurityUtils.getSecurityManager());
        Assert.assertEquals(manager, SecurityUtils.getSecurityManager());
    }

    @Test
    public void unBindSecurityManager()
    {
        WebSecurityManager manager = Mockito.mock(WebSecurityManager.class);

        SecurityInit
                .build()
                .bindSecurityManager(buildServletContext(manager));

        SecurityInit
                .build()
                .unBindSecurityManager();

        Assert.assertNull(ThreadContext.getSecurityManager());
    }

    @Nonnull
    private ServletContext buildServletContext(@Nonnull final WebSecurityManager manager )
    {
        WebEnvironment environment = Mockito.mock(WebEnvironment.class);
        Mockito.when(environment.getWebSecurityManager()).thenReturn(manager);

        ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getAttribute(Mockito.<String>any())).thenReturn(environment);

        return context;
    }
}
