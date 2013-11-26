package com.github.bednar.test;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.util.WebUtils;
import org.mockito.Mockito;

/**
 * @author Jakub Bednář (02/11/2013 17:07)
 */
public final class SecurityInit
{
    private static final SecurityInit instance = new SecurityInit();

    private SecurityInit()
    {
    }

    /**
     * @return singleton instance {@code SecurityInit}
     */
    @Nonnull
    public static SecurityInit build()
    {
        return instance;
    }

    /**
     * @param jetty with {@link ServletContext}
     *
     * @see #bindSecurityManager(javax.servlet.ServletContext)
     */
    @Nonnull
    public SecurityInit bindSecurityManager(@Nonnull final EmbeddedJetty jetty)
    {
        return bindSecurityManager(jetty.getServletContext());
    }

    /**
     * Bind {@link org.apache.shiro.web.mgt.WebSecurityManager} to actual {@link Thread}.
     *
     * @param context Servlet Context
     *
     * @return this
     */
    @Nonnull
    public SecurityInit bindSecurityManager(@Nonnull final ServletContext context)
    {
        SecurityManager securityManager = WebUtils
                .getRequiredWebEnvironment(context).getWebSecurityManager();

        SecurityUtils.setSecurityManager(securityManager);
        ThreadContext.bind(securityManager);

        return this;
    }

    /**
     * Unbind and destroy {@link SecurityManager} binded on actual {@link Thread}.
     *
     * @return this
     */
    @Nonnull
    public SecurityInit unBindSecurityManager()
    {
        SecurityManager securityManager = SecurityUtils.getSecurityManager();

        LifecycleUtils.destroy(securityManager);

        ThreadContext.unbindSecurityManager();
        SecurityUtils.setSecurityManager(null);

        return this;
    }

    /**
     * Create authenticated {@link Subject}
     *
     * @param principal username
     *
     * @return this
     */
    @Nonnull
    public SecurityInit buildSubject(@Nonnull final String principal)
    {
        Subject subject = buildSubject();

        Mockito.when(subject.isAuthenticated()).thenReturn(true);
        Mockito.when(subject.getPrincipal()).thenReturn(principal);

        ThreadContext.bind(subject);

        return this;
    }

    /**
     * Logout actual {@link Subject}
     *
     * @return this
     */
    @Nonnull
    public SecurityInit destroySubject()
    {
        Subject subject = buildSubject();
        subject.logout();

        Mockito.when(subject.isAuthenticated()).thenReturn(false);
        Mockito.when(subject.getPrincipal()).thenReturn(null);

        ThreadContext.bind(subject);

        return this;
    }

    @Nonnull
    private Subject buildSubject()
    {
        Subject subject = ThreadContext.getSubject();
        if (subject == null)
        {
            subject = Mockito.mock(Subject.class);
        }

        ThreadContext.bind(subject);

        return subject;
    }
}
