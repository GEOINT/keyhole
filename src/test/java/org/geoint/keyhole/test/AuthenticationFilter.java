package org.geoint.keyhole.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;

/**
 * Servlet filter that logs the header information of the ServletRequest and the
 * ServletResponse objects
 *
 */
@WebFilter
public class AuthenticationFilter implements Filter {

    private static final Logger logger
            = Logger.getLogger(AuthenticationFilter.class.getName());

    private String logFileName;
    private PrintWriter out;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            logger.log(
                    Level.INFO, "AuthenticationFilter initialized");
            Instant time = Instant.now();

            logFileName = filterConfig.getInitParameter("logFile");
            File logFile
                    = new File(
                            System.getProperty("java.io.tmpdir"), logFileName + time + ".log");
            out = new PrintWriter(new FileOutputStream(logFile, true));
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Cannot create authfilter log", ex);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //log the request values 
        out.println("*******request*******");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ArrayList<String> headerNames
                = Collections.list(httpRequest.getHeaderNames());
        for (String name : headerNames) {
            printHeader(name, httpRequest.getHeader(name));
        }

//log the response values 
        out.println(" ********response********");

        for (String name : httpResponse.getHeaderNames()) {
            printHeader(name, httpResponse.getHeader(name));
        }

        //append message to log Record 
//        AuthenticationLogRecord logRecord
//                = new AuthenticationLogRecord(Level.INFO, message.toString());
        //write log message to file
//        Instant time = Instant.now();
//
//        File logFile
//                = new File(
//                        System.getProperty("java.io.tmpdir"), logFileName + time + ".log");
//        try (PrintWriter writer = new PrintWriter(new FileOutputStream(logFile, true))) {
//
////            String logMessage = logRecord.getMessage();
//            if (logMessage != null) {
//                writer.println(logMessage);
//            }
//            writer.close();
//        } catch (Exception e) {
//            logger.log(
//                    Level.SEVERE, "unable to write log message to file {0}", e);
//        }
        chain.doFilter(request, new MyHttpResponse(httpResponse));
    }

    @Override
    public void destroy() {
        if (out != null) {
            out.flush();
            out.close();
        }
    }

    private void printHeader(String name, String value) {
        out.println(name + " : " + value + "\n");
    }

    private class MyHttpResponse implements HttpServletResponse {

        private HttpServletResponse delegate;

        public MyHttpResponse(HttpServletResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        public void addCookie(Cookie cookie) {
            delegate.addCookie(cookie);
        }

        @Override
        public boolean containsHeader(String name) {
            return delegate.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return delegate.encodeURL(url);
        }

        @Override
        public String encodeRedirectURL(String url) {
            return delegate.encodeRedirectURL(url);
        }

        @Override
        public String encodeUrl(String url) {
            return delegate.encodeUrl(url);
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return delegate.encodeRedirectUrl(url);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            delegate.sendError(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            delegate.sendError(sc);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            delegate.sendRedirect(location);
        }

        @Override
        public void setDateHeader(String name, long date) {
            delegate.setDateHeader(name, date);
        }

        @Override
        public void addDateHeader(String name, long date) {
            delegate.addDateHeader(name, date);
        }

        @Override
        public void setHeader(String name, String value) {
            printHeader(name, value);
            delegate.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            printHeader(name, value);
            delegate.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            delegate.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value) {
            delegate.addIntHeader(name, value);
        }

        @Override
        public void setStatus(int sc) {
            delegate.setStatus(sc);
        }

        @Override
        public void setStatus(int sc, String sm) {
            delegate.setStatus(sc, sm);
        }

        @Override
        public int getStatus() {
            return delegate.getStatus();
        }

        @Override
        public String getHeader(String name) {
            return delegate.getHeader(name);
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return delegate.getHeaders(name);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return delegate.getHeaderNames();
        }

        @Override
        public String getCharacterEncoding() {
            return delegate.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return delegate.getContentType();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return delegate.getOutputStream();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return delegate.getWriter();
        }

        @Override
        public void setCharacterEncoding(String charset) {
            delegate.setCharacterEncoding(charset);
        }

        @Override
        public void setContentLength(int len) {
            delegate.setContentLength(len);
        }

        @Override
        public void setContentLengthLong(long len) {
            delegate.setContentLengthLong(len);
        }

        @Override
        public void setContentType(String type) {
            delegate.setContentType(type);
        }

        @Override
        public void setBufferSize(int size) {
            delegate.setBufferSize(size);
        }

        @Override
        public int getBufferSize() {
            return delegate.getBufferSize();
        }

        @Override
        public void flushBuffer() throws IOException {
            delegate.flushBuffer();
        }

        @Override
        public void resetBuffer() {
            delegate.resetBuffer();
        }

        @Override
        public boolean isCommitted() {
            return delegate.isCommitted();
        }

        @Override
        public void reset() {
            delegate.reset();
        }

        @Override
        public void setLocale(Locale loc) {
            delegate.setLocale(loc);
        }

        @Override
        public Locale getLocale() {
            return delegate.getLocale();
        }

    }
}
