/*
 * Copyright 2000,2004 Anders Nyman.
 * 
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
package net.sf.j2ep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.j2ep.model.Server;

import java.util.logging.Logger;

/**
 * A filter that will locate the appropriate Rule and use it to rewrite any
 * incoming request to get the server targeted. Responses sent back are also
 * rewritten.
 *
 * @author Anders Nyman
 */
public class RewriteFilter implements Filter {

    private FilterConfig filterConfig;

    /**
     * Logging element supplied by commons-logging.
     */
    private static final Logger logger = Logger.getLogger("org.geoint.keyhole");

    /**
     * The server chain, will be traversed to find a matching server.
     */
    private ServerChain serverChain;

    /**
     * Rewrites the outgoing stream to make sure URLs and headers are correct.
     * The incoming request is first processed to identify what resource we want
     * to proxy.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        log("keyhole: RewriteFilter.doFilter() called  *******************");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        

        if (response.isCommitted()) {
            log("Not proxying, already committed.");
            logger.info("Not proxying, already committed.");
        } else if (!(request instanceof HttpServletRequest)) {
            log("Request is not HttpRequest will only handle HttpRequests.");
            logger.info("Request is not HttpRequest, "
                    + "will only handle HttpRequests.");
        } else if (!(response instanceof HttpServletResponse)) {
            log("Request is not HttpResponse will only handle HttpResponses.");
            logger.info("Request is not HttpResponse, "
                    + "will only handle HttpResponses.");
        } else {

            log("****************************standard request/response submitted");
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            //log request/response values
            log("*******request*******");

            ArrayList<String> headerNames
                    = Collections.list(httpRequest.getHeaderNames());
            for (String name : headerNames) {
                log(name + " : " + httpRequest.getHeader(name));
            }

            log(" ********response********");

            for (String name : httpResponse.getHeaderNames()) {
                log(name + " : " + httpResponse.getHeader(name));
            }

            //printing request/response headers to the console 
            //for debugging purposes
//            List<String> requestHeaders
//                    = Collections.list(httpRequest.getHeaderNames());
//
//            requestHeaders.stream().forEach((s) -> {
//                System.out.println("requestHeaderField: {0}" + s);
//                System.out.println("requestHeaderValue: {0}"
//                        + httpRequest.getHeader(s));
//            });
//
//            httpResponse.getHeaderNames().stream().forEach((s) -> {
//                System.out.println("responseHeaderField: {0}" + s);
//                System.out.println("responseHeaderValue: {0}"
//                        + httpResponse.getHeader(s));
//            });
//            System.out.println("httpRequest.getContextPath(): {0}"
//                    + httpRequest.getContextPath());
            //selecting the correct 'server' from the data.xml file
            Server server = serverChain.evaluate(httpRequest);
            if (server == null) {
                log("Could not find a rule for this request "
                        + "will not do anything.");
                logger.info("Could not find a rule for this request, "
                        + "will not do anything.");
                filterChain.doFilter(request, response);
            } else {
                log("server != null *******************");
                httpRequest.setAttribute("proxyServer", server);

                //print request/response headers to find the field needed for 
//                //the redirect
                //@todo retrieving the port from the config file, 
                //and not from the request url
                String ownHostName = server.getDomainName();
//                String ownHostName = request.getServerName() + ":" 
//                        + request.getServerPort();
//                logger.log(Level.WARNING, "ownHostName: {0}", ownHostName);

                UrlRewritingResponseWrapper wrappedResponse;
                wrappedResponse = new UrlRewritingResponseWrapper(httpResponse,
                        server, ownHostName, httpRequest.getContextPath(),
                        serverChain);
                
                

                
                filterChain.doFilter(httpRequest, wrappedResponse);

                wrappedResponse.processStream();
            }
        }
    }

    /**
     * Initialize.
     *
     * @param filterConfig
     * @throws javax.servlet.ServletException
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;

        String data = filterConfig.getInitParameter("dataUrl");
        if (data == null) {
            throw new ServletException("dataUrl is required.");
        } else {
            try {
                File dataFile = new File(filterConfig.getServletContext().getRealPath(data));
                ConfigParser parser = new ConfigParser(dataFile);
                serverChain = parser.getServerChain();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

    }

    /**
     * Release resources.
     *
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        serverChain = null;
    }

    private void log(String msg) {
        filterConfig.getServletContext().log("keyhole" + msg);
    }

}
