/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.keyhole;

import java.io.File;
import junit.framework.Assert;
import net.sf.j2ep.ProxyFilter;
import net.sf.j2ep.RewriteFilter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * STILL IN DEVELOPMENT, ONLY THING I NEED TO FINISH IS GETTING ARQUILLIAN 
 * DEFAULT SERVLET TEST TO "PASS THROUGH" THE PROXY, A NEW RULE I THINK 
 * I WILL NEED TO ADD =/
*/
/**
 * Tests HTTP GET requests using Glassfish Embedded
 */
//@RunWith(Arquillian.class)
public class HttpGetTest {

    private static final String FILTER_PROP_TEST_CONFIG = "dataUrl";
    private static final String FILTER_PROP_TEST_CONFIG_FILE = "/WEB-INF/classes/net/sf/j2ep/test/testData.xml";

//    @Deployment
//    @OverProtocol("Servlet 3.0")
//    public static Archive<?> init() {
//        return ShrinkWrap.create(WebArchive.class, "keyhole.war")
//                .addClass(ProxyFilter.class)
//                .addClass(RewriteFilter.class)
//                .addAsWebInfResource(new File("src/test/resources/WEB-INF/web.xml"))
//                .addAsWebResource(new File("src/test/resources/WEB-INF/testData.xml"))
//                ;
//    }
//    
//
//    @Test
//    public void testNothing() throws Exception {
//        Thread.sleep(10000);
//    }

//    public void beginNormalRequest(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/main.jsp", null, null);
//    }
//    
//    public void testNormalRequest() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endNormalRequest(WebResponse theResponse) {
//        assertEquals("The response code should be 200", 200, theResponse.getStatusCode());
//        assertEquals("Checking for correct page", "/GET/main.jsp", theResponse.getText());
//    }
//    
//    public void begin404(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/nonexistant.jsp", null, null);
//    }
//    
//    public void test404() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void end404(WebResponse theResponse) {
//        assertEquals("The response code should be 404", 404, theResponse.getStatusCode());
//    }
//    
//    public void beginNonExistentServer(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/testNonExistentServer/", null, null);
//    }
//    
//    public void testNonExistentServer() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endNonExistentServer(WebResponse theResponse) {
//        assertEquals("The response code should be 504", 504, theResponse.getStatusCode());
//    }
//    
//    public void beginConditional(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/image.gif", null, null);
//        theRequest.addHeader("If-Unmodified-Since", "Wed, 20 Jul 2000 15:00:00 GMT");
//    }
//    
//    public void testConditional() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endConditional(WebResponse theResponse) {
//        assertEquals("The response code should be 412", 412, theResponse.getStatusCode());
//    }
//    
//    public void beginUnhandledMethod(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/", null, null);
//    }
//    
//    public void testUnhandledMethod() throws ServletException, IOException {
//        
//        MethodWrappingRequest req = new MethodWrappingRequest(request, "JDFJDSJSN");
//        proxyFilter.doFilter(req, response, filterChain);
//
//    }
//
//    public void endUnhandledMethod(WebResponse theResponse) {
//        assertEquals("Checking that we got a 405 response", 405, theResponse.getStatusCode());
//        assertEquals("Correct options not returned",
//                "OPTIONS,GET,HEAD,POST,PUT,DELETE,TRACE", theResponse.getConnection()
//                        .getHeaderField("Allow"));
//    }
//    
//    public void begin405(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/405.jsp", null, null);
//    }
//    
//    public void test405() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void end405(WebResponse theResponse) {
//        assertEquals("The response code should be 405", 405, theResponse.getStatusCode());
//        String allow = theResponse.getConnection().getHeaderField("Allow");
//
//        assertTrue("Should include OPTIONS", allow.contains("OPTIONS"));
//        assertTrue("Should include GET", allow.contains("GET"));
//        assertFalse("Shouldn't include MYOWNHEADER", allow.contains("MYOWNHEADER"));
//        assertFalse("Shouldn't include PROPFIND", allow.contains("PROPFIND"));
//    }
//    
//    public void beginVia(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/main.jsp", null, null);
//    }
//    
//    public void testVia() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endVia(WebResponse theResponse) {
//        assertEquals("The response code should be 200", 200, theResponse.getStatusCode());
//        String via = theResponse.getConnection().getHeaderField("Via").trim();
//
//        try {
//            String serverHostName = InetAddress.getLocalHost().getHostName();  
//            String expected = "HTTP/1.1 " + serverHostName;
//            assertEquals("Checking that the via header is included", expected, via);
//        } catch (UnknownHostException e) {
//            fail("Couldn't get the hostname needed for header Via");
//        }
//    }
}
