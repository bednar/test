package com.github.bednar.test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.FragmentDescriptor;
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
    private String welcomeFile = "index.html";

    private Map<String, String> initParams = new HashMap<>();

    private Server server;

    @Nonnull
    public EmbeddedJetty start() throws Exception
    {
        server = new Server(port);

        context = new WebAppContext();

        context.setContextPath(contextPath);
        context.setResourceBase(resourceBase);
        context.setServer(server);
        context.setWelcomeFiles(new String[]{welcomeFile});

        for (String key : initParams.keySet())
        {
            context.setInitParameter(key, initParams.get(key));
        }

        MetaData metaData = context.getMetaData();

        Resource webXML = Resource.newClassPathResource(descriptor);
        metaData.setWebXml(webXML);

        //Web Fragments
        if (webFragments)
        {
            Map<String, Resource> fragments = findFragments();

            //apply order?
            if (!metaData.getWebXml().getOrdering().isEmpty())
            {
                for (String name : metaData.getWebXml().getOrdering())
                {
                    Resource fragmentXML = fragments.get(name);

                    addFragment(fragmentXML, webXML, metaData);
                }
            }
            else
            {
                for (Resource fragmentXML : fragments.values())
                {
                    addFragment(fragmentXML, webXML, metaData);
                }
            }
        }

        server.setHandler(context);
        server.start();
        server.setStopAtShutdown(true);

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
    public EmbeddedJetty port(@Nonnull final Integer port)
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
    public EmbeddedJetty webFragments(@Nonnull final Boolean webFragments)
    {
        this.webFragments = webFragments;

        return this;
    }

    /**
     * @see org.eclipse.jetty.webapp.WebAppContext#setContextPath(String)
     */
    @Nonnull
    public EmbeddedJetty contextPath(@Nonnull final String contextPath)
    {
        this.contextPath = contextPath;

        return this;
    }

    /**
     * @see org.eclipse.jetty.webapp.WebAppContext#setResourceBase(String)
     */
    @Nonnull
    public EmbeddedJetty resourceBase(@Nonnull final String resourceBase)
    {
        this.resourceBase = resourceBase;

        return this;
    }

    /**
     * @see org.eclipse.jetty.webapp.WebAppContext#setDescriptor(String)
     */
    @Nonnull
    public EmbeddedJetty descriptor(@Nonnull final String descriptor)
    {
        this.descriptor = descriptor;

        return this;
    }

    /**
     * @see javax.servlet.ServletContext#setInitParameter(java.lang.String, java.lang.String)
     */
    @Nonnull
    public EmbeddedJetty initParameter(@Nonnull final String key, @Nullable final String value)
    {
        this.initParams.put(key, value);

        return this;
    }

    /**
     * @see org.eclipse.jetty.server.handler.ContextHandler#setWelcomeFiles
     */
    @Nonnull
    public EmbeddedJetty welcomeFile(@Nonnull final String welcomeFile)
    {
        this.welcomeFile = welcomeFile;

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
     * Nonnull after start
     */
    @Nonnull
    public WebAppContext getWebAppContext()
    {
        return context;
    }

    /**
     * @return Base URL "deployed" web application
     */
    @Nonnull
    public String getURL(@Nonnull final String... paths)
    {
        return context.getServer().getURI().toASCIIString() + StringUtils.join(paths, "/");
    }

    /**
     * @return Jetty Server
     */
    @Nullable
    public Server getServer()
    {
        return server;
    }

    @Nonnull
    private Map<String, Resource> findFragments() throws Exception
    {
        HashMap<String, Resource> results = new HashMap<>();

        Enumeration<URL> resources = this.getClass().getClassLoader().getResources(WEB_FRAGMENT_RESOURCE);
        while (resources.hasMoreElements())
        {
            Resource fragment = Resource.newResource(resources.nextElement());

            FragmentDescriptor fragmentDescriptor = new FragmentDescriptor(fragment);
            fragmentDescriptor.parse();

            results.put(fragmentDescriptor.getName(), fragment);
        }

        return results;
    }

    private void addFragment(@Nonnull final Resource fragmentXML,
                             @Nonnull final Resource webXML,
                             @Nonnull final MetaData metaData) throws Exception
    {
        Resource fragmentDir = Resource.newResource(fragmentXML.getURL().getFile().replace(WEB_FRAGMENT_RESOURCE, ""));

        if (!fragmentXML.equals(webXML))
        {
            metaData.getOrderedWebInfJars().add(fragmentDir);
            metaData.addFragment(fragmentDir, fragmentXML);
        }
    }
}
