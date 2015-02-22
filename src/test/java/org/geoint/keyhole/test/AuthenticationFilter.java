package org.geoint.keyhole.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.servlet.annotation.WebFilter;


/**
 * Servlet filter that logs the header information of the
 * ServletRequest and the ServletResponse objects
 * 
 */
@WebFilter
public class AuthenticationFilter implements Filter {

    private static final Logger logger
            = Logger.getLogger(AuthenticationFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.log(
                Level.INFO, "AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        //log the response values 
        StringBuilder message
                = new StringBuilder(" ********response******** ").append("\n");
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        for (String name : httpResponse.getHeaderNames()) {
            message.append(name).append(" : ")
                    .append(httpResponse.getHeader(name)).append("\n");
        }

        //log the request values 
        message = new StringBuilder("*******request*******").append("\n");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ArrayList<String> headerNames
                = Collections.list(httpRequest.getHeaderNames());
        for (String name : headerNames) {
            message.append(name).append(" : ")
                    .append(httpRequest.getHeader(name)).append("\n");
        }

        //append message to log Record 
        AuthenticationLogRecord logRecord
                = new AuthenticationLogRecord(Level.INFO, message.toString());

        //write log message to file
        File logFile
                = new File(
                        System.getProperty("java.io.tmpdir"), "proxy-dump.log");
        try (PrintWriter writer = new PrintWriter(logFile)) {

            String logMessage = logRecord.getMessage();
            if (logMessage != null) {
                writer.println(logMessage);
            }
            writer.close();
        } catch (Exception e) {
            logger.log(
                    Level.SEVERE, "unable to write log message to file {0}", e);
        }

    }

    @Override
    public void destroy() {

    }

}
