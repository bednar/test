package com.github.bednar.test;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author Jakub Bednář (01/09/2013 11:23 AM)
 */
public final class EmbeddedJetty
{
    private static final String WEB_FRAGMENT_RESOURCE = "META-INF/web-fragment.xml";

    private WebAppContext context;

    private Integer port = 8090;
    private Boolean webFragments = false;

    private String contextPath = "/";
    private String resourceBase = "src/main/webapp";
    private String descriptor = "META-INF/web-fragment.xml";

    @Nonnull
    public EmbeddedJetty start() throws Exception
    {
        Server jetty = new Server(port);

        context = new WebAppContext();

        context.setContextPath(contextPath);
        context.setResourceBase(resourceBase);
        context.setServer(jetty);

        MetaData metaData = context.getMetaData();

        Resource webXML = Resource.newClassPathResource(descriptor);
        metaData.setWebXml(webXML);

        //Web Fragments
        if (webFragments)
        {
            for (URL fragment : getWebFragments())
            {
                Resource fragmentXML = Resource.newResource(fragment);
                Resource fragmentDir = Resource.newResource(fragment.getFile().replace(WEB_FRAGMENT_RESOURCE, ""));

                if (!fragmentXML.equals(webXML))
                {
                    metaData.getOrderedWebInfJars().add(fragmentDir);
                    metaData.addFragment(fragmentDir, fragmentXML);
                }
            }
        }

        jetty.setHandler(context);
        jetty.start();
        jetty.setStopAtShutdown(true);

        return this;
    }

    @Nonnull
    public EmbeddedJetty stop() throws Exception
    {
        Server server = context.getServer();
        server.stop();

        return this;
    }

    /**
     * @see org.eclipse.jetty.server.Server#Server(int)
     */
    @Nonnull
    public EmbeddedJetty port(final @Nonnull Integer port)
    {
        this.port = port;

        return this;
    }

    /**
     * @param webFragments if {@link Boolean#TRUE} than use classpath webfragments otherwise use standard jetty
     *                     configuration
     *
     * @return this
     */
    @Nonnull
    public EmbeddedJetty webFragments(final @Nonnull Boolean webFragments)
    {
        this.webFragments = webFragments;

        return this;
    }

    /**
     * @see org.eclipse.jetty.webapp.WebAppContext#setContextPath(String)
     */
    @Nonnull
    public EmbeddedJetty contextPath(final @Nonnull String contextPath)
    {
        this.contextPath = contextPath;

        return this;
    }

    /**
     * @see org.eclipse.jetty.webapp.WebAppContext#setResourceBase(String)
     */
    @Nonnull
    public EmbeddedJetty resourceBase(final @Nonnull String resourceBase)
    {
        this.resourceBase = resourceBase;

        return this;
    }

    /**
     * @see org.eclipse.jetty.webapp.WebAppContext#setDescriptor(String)
     */
    @Nonnull
    public EmbeddedJetty descriptor(final @Nonnull String descriptor)
    {
        this.descriptor = descriptor;

        return this;
    }

    /**
     * @return servlet context of Jetty Web Server
     */
    @Nonnull
    public ServletContext getServletContext()
    {
        return context.getServletContext();
    }

    /**
     * @return Base URL "deployed" web application
     */
    @Nonnull
    public String getURL()
    {
        return context.getServer().getURI().toASCIIString();
    }

    @Nonnull
    private List<URL> getWebFragments() throws IOException
    {
        Enumeration<URL> resources = this.getClass().getClassLoader().getResources(WEB_FRAGMENT_RESOURCE);

        return Collections.list(resources);
    }
}
