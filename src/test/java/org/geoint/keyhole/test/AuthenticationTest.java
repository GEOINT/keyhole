package org.geoint.keyhole.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests Client / Server authentication over Https.
 *
 */
public class AuthenticationTest {
//
//    private static final String AUTHENTICATION_TEST_CONFIG = "dataUrl";
//
//    private static final String AUTHENTICATION_PROPERTY_TEST_CONFIG
//            = "/WEB-INF/classes/net/jf/j2ep/test/testData.xml";

    private static final Logger logger
            = Logger.getLogger(AuthenticationTest.class.getName());

    private static final Map<String, String> writtenUrls = new HashMap<>();
    private static GlassFish glassfish;
    private static GlassFishRuntime glassfishRuntime;

    @BeforeClass
    public static void startUp() {
        //start gf instances here
        try {

            //create webarchive files (.war files) to be deployed to embedded
            //glassfish instances to monitor how keyhole rewrites/proxies 
            //authentication over https, specifically the glassfish admin 
            //console login page 
            WebArchive keyholeArchive = ShrinkWrap
                    .create(WebArchive.class, "keyhole.war")
                    .addPackage("net.sf.j2ep")
                    .addClass("org.geoint.keyhole.test.AuthenticationFilter")
                    .addAsWebResource(
                            new File("src/test/resources/keyhole/WEB-INF/testData.xml"))
                    .addAsWebInfResource(
                            new File("src/test/resources/keyhole/WEB-INF/web.xml"));

            File keyhole = new File(
                    System.getProperty("glassfish.embedded.tmpdir", "keyhole.war"));
            keyholeArchive.as(ZipExporter.class).exportTo(keyhole, true);

            WebArchive testAuthHeaderArchive = ShrinkWrap
                    .create(WebArchive.class, "testAuthHeader.war")
                    .addClass("org.geoint.keyhole.test.AuthenticationFilter")
                    .addClass("org.geoint.keyhole.test.AuthenticationServlet")
                    .addAsWebInfResource(
                            new File("src/test/resources/testAuth/WEB-INF/web.xml"));

            File testAuthHeader = new File(
                    System.getProperty("glassfish.embedded.tmpdir", "testAuthHeader.war"));
            testAuthHeaderArchive.as(ZipExporter.class)
                    .exportTo(testAuthHeader, true);

            //stand up embedded glassfish instance and deploy archives                                    
            GlassFishProperties glassfishProperties = new GlassFishProperties();

            //needed?
            glassfishProperties.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
            glassfishProperties.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
            glassfishProperties.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
            glassfishProperties.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

            glassfishProperties.setPort("https-listener", 8181);
            glassfishProperties.setPort("http-listener", 8080);

            try {
                glassfishRuntime = GlassFishRuntime.bootstrap();
            } catch (GlassFishException e) {
                logger.log(Level.WARNING, "glassfishRuntime already bootstrapped {0}", e);
            }
            if (glassfishRuntime != null) {
                glassfish = glassfishRuntime.newGlassFish(glassfishProperties);

                glassfish.start();
                glassfish.getDeployer().deploy(keyhole, "--name=keyhole", "--contextroot=keyhole");
                glassfish.getDeployer().deploy(testAuthHeader, "--name=test", "--contextroot=test");
            }
        } catch (GlassFishException e) {
            logger.log(
                    Level.SEVERE, "unable to standup glassfish runtime {0}", e);
        }

    }

    @AfterClass
    public static void cleanUp() {

        try {
            //undeploy applications before shutting down
            if (glassfish != null) {
                for (String app : glassfish
                        .getDeployer().getDeployedApplications()) {
                    glassfish.getDeployer().undeploy(app);
                }

                glassfish.stop();
            }
            if (glassfishRuntime != null) {
                glassfishRuntime.shutdown();
            }

        } catch (GlassFishException e) {
            logger.log(Level.SEVERE,
                    "unable to shut down embedded glassfish runtime {0}", e);
        }
    }

    /**
     * get the horse out of the gate
     */
    @Test
    @Ignore
    public void testNothingBase() {
        assertTrue(true);
    }

    //@todo update the junit assertions to test for valid responses
    /**
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testDirectGet() throws Exception {
        URL url = new URL("http://localhost:8080/test/test");

        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("GET");
        cnx.connect();

        String responseMessage = cnx.getResponseMessage();

        BufferedReader responseBody = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
        String line;
        System.out.println("&&&&&&&&&&&&&& response body  &&&&&&&&&&&&");
        while ((line = responseBody.readLine()) != null) {
            System.out.println(line);
        }

        logger.log(Level.INFO, "**********response message: {0}", responseMessage);
        assertNotNull(responseMessage);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testDirectPost() throws Exception {
        URL url = new URL("http://localhost:8080/test/test");

        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("POST");
        cnx.connect();
        logger.log(Level.INFO, "**********response code: {0}", cnx.getResponseCode());
        assertNotNull(cnx.getResponseMessage());
    }

    /**
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testDirectPostOverHttps() throws Exception {
        URL url = new URL("https://localhost:8181/test/test");

        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("POST");
        cnx.addRequestProperty("jusername", "admin");
        cnx.addRequestProperty("jpassword", "scarlet8");
        cnx.connect();

        logger.log(Level.INFO, "**********response code: {0}", cnx.getResponseCode());
        assertNotNull(cnx.getResponseMessage());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testDirectGetOverHttps() throws Exception {
        URL url = new URL("https://localhost:8181/test/test");

        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("GET");
        cnx.connect();

        String responseMessage = cnx.getResponseMessage();
        logger.log(Level.INFO, "**********response message: {0}", responseMessage);
        assertNotNull(responseMessage);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testProxiedGet() throws Exception {
        URL url = new URL("http://localhost:8080/keyhole/proxyTest");

        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("GET");
        cnx.connect();

        String responseMessage = cnx.getResponseMessage();
        logger.log(Level.INFO, "**********response message: {0}", responseMessage);
        assertNotNull(responseMessage);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testProxiedPost() throws Exception {

        URL url = new URL("http://localhost:8080/keyhole/proxyTest");

        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("POST");
        cnx.connect();
        logger.log(Level.INFO, "**********response message: {0}", cnx.getResponseCode());
        assertNotNull(cnx.getResponseMessage());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testProxiedHttpsGet() throws Exception {
        URL url = new URL("https://localhost:8181/keyhole/proxyHttpsTest/test");

        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("GET");
        cnx.connect();

        String responseMessage = cnx.getResponseMessage();
        BufferedReader responseBody = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
        String line;
        System.out.println("&&&&&&&&&&&&&& response body  &&&&&&&&&&&&");
        while ((line = responseBody.readLine()) != null) {
            System.out.println(line);
        }
        logger.log(Level.INFO, "**********response message: {0}", cnx.getResponseCode());
        assertNotNull(responseMessage);
    }

    /**
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testProxiedHttpsPost() throws Exception {
        URL url = new URL("https://localhost:8181/keyhole/proxyHttpsTest/test");
        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        cnx.setRequestMethod("POST");
        cnx.addRequestProperty("j_username", "admin");
        cnx.addRequestProperty("j_password", "scarlet8");
        cnx.connect();
        String line;
        System.out.println("&&&&&&&&&&&&&& response body  &&&&&&&&&&&&");
        BufferedReader responseBody = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
        while ((line = responseBody.readLine()) != null) {
            System.out.println(line);
        }
        logger.log(Level.INFO, "**********response message: {0}  ******************", cnx.getResponseMessage());
        assertNotNull(cnx.getResponseMessage());
    }

}
