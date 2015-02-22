package org.geoint.keyhole.test;

import java.nio.file.Files;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.embeddable.BootstrapProperties;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests Client / Server authentication over Https.
 *
 */
//@RunWith(Arquillian.class)
public class AuthenticationTest {

    private static final String AUTHENTICATION_TEST_CONFIG = "dataUrl";

    private static final String AUTHENTICATION_PROPERTY_TEST_CONFIG
            = "/WEB-INF/classes/net/jf/j2ep/test/testData.xml";

    private static final Logger logger
            = Logger.getLogger(AuthenticationTest.class.getName());

    private static final Map<String, String> writtenUrls = new HashMap<>();
    private static GlassFish glassfish1;
    private static GlassFish glassfish2;
    private static GlassFishRuntime glassfishRuntime;

    @BeforeClass
    public static void startUp() {
        //start gf instances here
        try {

            //create webarchive files (.war files) to be deployed to embedded
            //glassfish instances to monitor how keyhole rewrites/proxies 
            //authentication over https, specifically the glassfish admin 
            //console login page 
            WebArchive firstListener = ShrinkWrap
                    .create(WebArchive.class, "first-listener.war")
                    .addClass("org.geoint.keyhole.test.AuthenticationFilter")
                    .addClass("org.geoint.keyhole.test.AuthenticationServlet")
                    .addAsWebInfResource(
                            new File("src/test/resources/WEB-INF/web.xml"));
            File firstListenerFile = new File(
                    System.getProperty("java.io.tmpdir", "first-listener.war"));
            firstListener.as(ZipExporter.class).exportTo(firstListenerFile, true);

            WebArchive secondListener = ShrinkWrap
                    .create(WebArchive.class, "servlet-listener.war")
                    .addClass("org.geoint.keyhole.test.AuthenticationFilter")
                    .addClass("org.geoint.keyhole.test.AuthenticationServlet")
                    .addAsWebInfResource(
                            new File("src/test/resources/WEB-INF/web.xml"));

            File secondListenerFile = new File(
                    System.getProperty("java.io.tmpdir", "second-listener.war"));
            secondListener.as(ZipExporter.class)
                    .exportTo(secondListenerFile, true);

            //configure and start first embedded glassfish instance
            BootstrapProperties bootstrapProperties = new BootstrapProperties();
            GlassFishProperties glassfishProperties = new GlassFishProperties();

            glassfishRuntime
                    = GlassFishRuntime.bootstrap(bootstrapProperties);
            glassfishProperties.setPort("https-listener", 8181);

            glassfish1 = glassfishRuntime.newGlassFish(glassfishProperties);

            glassfish1.start();
            glassfish1.getDeployer().deploy(firstListenerFile);

            //configure and start second glassfish instance
            //update properties files
            glassfishProperties.setPort("http-listener", 8080);

            glassfish2 = glassfishRuntime.newGlassFish(glassfishProperties);
            glassfish2.start();
            glassfish2.getDeployer().deploy(secondListenerFile);

        } catch (GlassFishException e) {
            logger.log(
                    Level.SEVERE, "unable to standup glassfish runtime {0}", e);
        }

    }

    @AfterClass
    public static void cleanUp() {

        try {
            //undeploy applications before shutting down
            for (String app : glassfish1
                    .getDeployer().getDeployedApplications()) {
                glassfish1.getDeployer().undeploy(app);
            }

            for (String app : glassfish2
                    .getDeployer().getDeployedApplications()) {
                glassfish2.getDeployer().undeploy(app);
            }

            glassfish1.stop();
            glassfish2.stop();
            glassfishRuntime.shutdown();

        } catch (GlassFishException e) {
            logger.log(Level.SEVERE,
                    "unable to shut down embedded glassfish runtime {0}", e);
        }
    }

    //webarchive for use by the arquillian testing framework
//    @Deployment
//    @TargetsContainer("glassfish-embed")
//    @OverProtocol("Servlet 3.0")
//    public static Archive<?> init() {
//
//        return ShrinkWrap.create(WebArchive.class, "keyhole.war")
//                .addPackage("net.sf.j2ep")
//                .addClass("org.geoint.keyhole.test.AuthenticationFilter")                
//                .addAsWebInfResource(
//                        new File("src/test/resources/WEB-INF/web.xml"))
//                .addAsWebResource(
//                        new File("src/test/resources/WEB-INF/testData.xml"));
//    }
//
//    @ArquillianResource
//    URL baseUrl;
    /**
     * get the horse out of the gate
     */
    @Test
    @RunAsClient
    public void testNothingBase() {
        assertTrue(true);
    }

    /**
     *
     * @throws Exception
     */
//    @Test
//    @RunAsClient
//    public void testPost() throws Exception {
//
//        URL url = new URL(baseUrl.toString() + "AuthenticationServlet");
//
//        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
//        cnx.setRequestMethod("POST");
//        cnx.connect();
//
//        String responseMessage = cnx.getResponseMessage();
//        logger.log(Level.INFO, "response message: {0}", responseMessage);
//        assertNotNull(responseMessage);
//    }
    /**
     * basic servlet GET test
     *
     * @throws java.lang.Exception
     */
//    @Ignore
//    @Test
//    @RunAsClient
//    public void testGet() throws Exception {
//
//        URL url = new URL(baseUrl.toString() + "/keyhole/gf/");
//
//        HttpsURLConnection cnx = (HttpsURLConnection) url.openConnection();
//        cnx.setRequestMethod("GET");
//        cnx.connect();
//
//        assertTrue(true);
//    }
//}
//
//    @Deployment(name = "servletListener", order = 2)
//    @TargetsContainer("glassfish-embedded")  //@OverProtocol("Servlet 3.0")   
//    public static Archive<?> init1() {
//        return ShrinkWrap.create(WebArchive.class, "servlet-listener-4848.war")
//                .addClass("org.geoint.keyhole.test.AuthenticationServlet")
//                .addClass("org.geoint.keyhole.test.AuthenticationTestFilter");
//    }
//    
//    @Deployment(name = "logger", order = 3)
//    @TargetsContainer("container3")
//    @OverProtocol("Servlet 3.0")
//    public static Archive<?> init2(){
//        return ShrinkWrap.create(WebArchive.class, "logReader.war")
//                .addClass("org.geoint.keyhole.test.AuthenticationLogRecord")
//                .addClass("org.geoint.keyhole.test.LogReceiverServlet");                
//    }
//    
}
