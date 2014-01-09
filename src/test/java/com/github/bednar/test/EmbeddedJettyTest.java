package com.github.bednar.test;

import org.eclipse.jetty.server.NetworkConnector;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (26/11/2013 16:33)
 */
public class EmbeddedJettyTest
{
    @Test
    public void startAndStop() throws Exception
    {
        new EmbeddedJetty()
                .descriptor("web-fragment.xml")
                .webFragments(false)
                .start()
                .stop();
    }

    @Test
    public void startOnCustomPort() throws Exception
    {
        EmbeddedJetty jetty = new EmbeddedJetty()
                .descriptor("web-fragment.xml")
                .port(2356)
                .start();

        Assert.assertNotNull(jetty.getServer());
        Assert.assertEquals(2356, ((NetworkConnector)jetty.getServer().getConnectors()[0]).getPort());

        jetty.stop();
    }

    @Test
    public void servletContextNotNull() throws Exception
    {
        EmbeddedJetty jetty = new EmbeddedJetty()
                .descriptor("web-fragment.xml")
                .port(2455)
                .start();
        
        Assert.assertNotNull(jetty.getServletContext());
        
        jetty.stop();
    }

    @Test
    public void urlValue() throws Exception
    {
        EmbeddedJetty jetty = new EmbeddedJetty()
                .descriptor("web-fragment.xml")
                .port(2345)
                .start();

        Assert.assertTrue(jetty.getURL().endsWith("2345/"));

        jetty.stop();
    }

    @Test
    public void urlValueWithPaths() throws Exception
    {
        EmbeddedJetty jetty = new EmbeddedJetty()
                .descriptor("web-fragment.xml")
                .port(2348)
                .start();

        String url = jetty.getURL("api", "beer", "3");

        Assert.assertTrue(url.endsWith("/api/beer/3"));

        jetty.stop();
    }

    @Test
    public void lookupToFragments() throws Exception
    {
        new EmbeddedJetty()
                .descriptor("web-fragment.xml")
                .webFragments(true)
                .port(2345)
                .start()
                .stop();
    }

    @Test
    public void initParameter() throws Exception
    {
        EmbeddedJetty jetty = new EmbeddedJetty()
                .descriptor("web-fragment.xml")
                .webFragments(false)
                .port(2111)
                .initParameter("some-param", "some-value")
                .start();

        Assert.assertEquals("some-value", jetty.getServletContext().getInitParameter("some-param"));
    }
}
