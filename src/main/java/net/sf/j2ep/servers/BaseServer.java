/*
 * Copyright 2005 Anders Nyman.
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
package net.sf.j2ep.servers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.j2ep.model.Server;

/**
 * A basic implementation of the Server interface using a single host name to
 * map all connections. Can be easily extended to create a server that gets it
 * host name in some other way. For instance a server fetching the host name
 * from the request could be made to change the proxy into a forwarding proxy.
 *
 * @author Anders Nyman
 */
/**
 *
 *
 * @author Anders Nyman
 */
public class BaseServer extends ServerContainerBase implements Server {

    private static final Logger logger = Logger.getLogger(BaseServer.class.getName());

    /**
     * Marks if this rule server will do any rewriting of links.
     */
    private boolean isRewriting;

    /**
     * The URL scheme identifying the protocol to use to communicate with the
     * server.
     */
    private String scheme;
    /**
     * The host and port for this server
     */
    private String domainName;

    /**
     * The path for this server
     */
    private String path;

    /**
     * Basic constructor that will initialize the directory to "".
     */
    public BaseServer() {
        logger.log(Level.SEVERE, "-----------------------base server reinitialized --------------");
        path = "";
        isRewriting = false;
    }

    /**
     * @param request
     * @return
     * @see
     * net.sf.j2ep.model.ServerContainer#getServer(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public Server getServer(HttpServletRequest request) {
        return this;
    }

    /**
     * Will no do any handling
     *
     * @see
     * net.sf.j2ep.model.Server#preExecute(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public HttpServletRequest preExecute(HttpServletRequest request) {
        return request;
    }

    /**
     * Will no do any handling
     *
     * @see
     * net.sf.j2ep.model.Server#postExecute(javax.servlet.http.HttpServletResponse)
     */
    @Override
    public HttpServletResponse postExecute(HttpServletResponse response) {
        return response;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getScheme() {
        return scheme;
    }

    /**
     * @see net.sf.j2ep.model.Server#getDomainName()
     */
    @Override
    public String getDomainName() {
        return domainName;
    }

    /**
     * @see net.sf.j2ep.model.Server#getPath()
     */
    @Override
    public String getPath() {
        return path;
    }

    /**
     * @param location
     * @return
     * @see net.sf.j2ep.model.ServerContainer#getServerMapped(java.lang.String)
     */
    @Override
    public Server getServerMapped(String location) {
        String fullPath = getDomainName() + getPath() + "/";
        if (location.startsWith(fullPath) && isRewriting) {
            return this;
        } else {
            return null;
        }
    }

    /**
     * Set if this server wants absolute links mapped for this server to be
     * rewritten.
     *
     * @param rewrite Should be true if we want to do rewriting
     */
    public void setIsRewriting(String rewrite) {
        if (rewrite != null && rewrite.equals("true")) {
            isRewriting = true;
        }
    }

    public void setScheme(String scheme) {
        if (scheme == null || scheme.isEmpty()) {
            throw new IllegalArgumentException("URL scheme must not be null");
        }
        this.scheme = scheme;
    }

    /**
     * Sets the host and port we are mapping to.
     *
     * @param domainName Value to set
     */
    public void setDomainName(String domainName) {
        if (domainName == null) {
            throw new IllegalArgumentException(
                    "The hostAndPort string cannot be null.");
        } else {
            this.domainName = domainName;
        }
    }

    /**
     * Sets the path we are mapping to.
     *
     * @param path The path
     */
    public void setPath(String path) {
        if (path != null) {
            this.path = path;
        }
    }

    /**
     * @see
     * net.sf.j2ep.model.Server#setConnectionExceptionRecieved(java.lang.Exception)
     */
    @Override
    public void setConnectionExceptionRecieved(Exception e) {
        logger.log(Level.SEVERE, "BaseServer#setConnectionExceptionReceived {0}", e);
    }
}
