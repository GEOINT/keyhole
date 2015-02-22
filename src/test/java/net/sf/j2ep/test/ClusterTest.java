package net.sf.j2ep.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import static junit.framework.Assert.assertEquals;

import net.sf.j2ep.ProxyFilter;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ClusterTest {  // extends FilterTestCase {
//
//    private ProxyFilter proxyFilter;
//    private static final Logger logger = Logger.getLogger(ClusterTest.class.getName());
//
//    @BeforeClass
//    public void setUp() {
//        proxyFilter = new ProxyFilter();
//
//        try {
//            //config.setInitParameter("dataUrl", "/WEB-INF/classes/net/sf/j2ep/test/testData.xml");
//            MockFilterConfig config = new MockFilterConfig();  
//            config.addInitParameter("dataUrl", "/WEB-INF/classes/net/sf/j2ep/test/testData.xml");
//            proxyFilter.init(config);
//        } catch (ServletException e) {
//            logger.log(Level.SEVERE, "Problem with init, error given was {0}", e.getMessage());
//            assertTrue(false);
//        }
//
//    }

//    @Ignore
//    @Test
//    public void testServer() {
//
//        try {
//            //        theRequest.setURL("localhost:8080", "/test", "/testCluster/serverId.jsp", null, null);
//            URI uri = new URI("localhost:8080/test/testCluster/serverId.jsp");
//            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(uri);
//            org.springframework.mock.web.MockHttpServletRequest request = requestBuilder.header("Cookie", "JSESSIONID=somesessionid.server0").buildRequest(null);
//            MockHttpServletResponse response = new MockHttpServletResponse();
//            MockFilterChain filterChain = new MockFilterChain();  //may need to pass a test servlet in here...        
//
//            proxyFilter.doFilter(request, response, filterChain);
//            assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
//            
//        } catch (IOException | ServletException | URISyntaxException e) {
//            logger.log(Level.SEVERE, "testServerCluster failed: {0}", e);
//            assertTrue(false);
//        }
//        assertTrue(true);
//    }

//    public void beginSecondServer(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/testCluster/serverId.jsp", null, null);
//        theRequest.addHeader("Cookie", "JSESSIONID=somesessionid.server1");
//    }
//
//    public void testSecondServer() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//
//    public void endSecondServer(WebResponse theResponse) {
//        assertEquals("Targeting second server", 1, Integer.parseInt(theResponse.getText()));
//    }
//
//    public void beginSessionRewriting(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/testCluster/createSession.jsp", null, null);
//    }
//
//    public void testSessionRewriting() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//
//    public void endSessionRewriting(WebResponse theResponse) {
//        String session = theResponse.getCookie("JSESSIONID").getValue();
//        assertTrue("Checking that we have rewritten the session", session.contains(".server"));
//    }
//
//    public void beginServerRemoving(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/testCluster/viewSession.jsp", null, null);
//        theRequest.addHeader("Cookie", "JSESSIONID=somesessionid.server1");
//    }
//
//    public void testServerRemoving() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//
//    public void endServerRemoving(WebResponse theResponse) {
//        assertEquals("Checking that the session is included", "somesessionid", theResponse.getText().trim());
//    }
//
//    public void beginNonExistingServer(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/testCluster/serverId.jsp", null, null);
//        theRequest.addHeader("Cookie", "JSESSIONID=somesessionid.server54");
//    }
//
//    public void testNonExistingServer() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//
//    public void endNonExistingServer(WebResponse theResponse) {
//        int id = Integer.parseInt(theResponse.getText());
//        String session = theResponse.getCookie("JSESSIONID").getValue();
//        assertTrue("Checking that the cluster did send the response to some server anyways", id == 0 || id == 1);
//        assertTrue("Checking that we now have a new server", session.endsWith(".server" + id));
//    }
}
