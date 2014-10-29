//package net.sf.j2ep.test;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.servlet.ServletException;
//
//import net.sf.j2ep.ProxyFilter;
//
//import org.apache.cactus.FilterTestCase;
//import org.apache.cactus.WebRequest;
//import org.apache.cactus.WebResponse;
//
//public class TraceTest extends FilterTestCase {
//
//    private ProxyFilter proxyFilter;
//
//    @Override
//    public void setUp() {
//        
//        proxyFilter = new ProxyFilter();
//
//        config.setInitParameter("dataUrl",
//                "/test-classes/testData.xml");
//        
//        try {
//            proxyFilter.init(config);
//        } catch (ServletException e) {
//            fail("Problem with init, error given was " + e.getMessage());
//        }
//    }
//
//    public void beginNoMaxFowards(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/", null, null);
//        theRequest.addHeader("Via", "HTTP/1.0 fakeserver.com");
//    }
//
//    public void testNoMaxFowards() throws ServletException, IOException {
//        MethodWrappingRequest req = new MethodWrappingRequest(request, "TRACE");
//        proxyFilter.doFilter(req, response, filterChain);
//    }
//
//    public void endNoMaxFowards(WebResponse theResponse) {
//        String serverHostName = null;
//        try {
//            serverHostName = InetAddress.getLocalHost().getHostName();
//        } catch (UnknownHostException e) {
//            fail("Couldn't get the hostname needed for header Via");
//        }
//
//        String contentType = theResponse.getConnection().getContentType();
//        assertEquals("Checking content-type", "message/http", contentType);
//
//        String expectedVia = "HTTP/1.0 fakeserver.com, " + "HTTP/1.1 " + serverHostName;
//        assertTrue("Checking that the via header is included", theResponse.getText().contains(expectedVia));
//
//        String expectedUserAgent = "Jakarta Commons-HttpClient/3.0-rc3";
//        assertTrue("Checking that user-agent is included", theResponse.getText().contains(expectedUserAgent));
//    }
//
//    public void beginMaxForwards(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/maxForwards", null, null);
//        theRequest.addHeader("Via", "HTTP/1.0 fakeserver.com");
//        theRequest.addHeader("Max-Forwards", "0");
//    }
//
//    public void testMaxForwards() throws ServletException,
//            IOException {
//        MethodWrappingRequest req = new MethodWrappingRequest(request, "TRACE");
//        proxyFilter.doFilter(req, response, filterChain);
//    }
//
//    public void endMaxForwards(WebResponse theResponse) {
//        String contentType = theResponse.getConnection().getContentType();
//        if (contentType.contains(";")) {
//            contentType = contentType.substring(0, contentType.indexOf(";"));
//        }
//        assertEquals("Checking content-type", "message/http", contentType);
//
//        String expectedVia = "HTTP/1.0 fakeserver.com";
//        assertTrue("Checking that the via header is included", theResponse.getText().contains(expectedVia));
//    }
//}
