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
package net.sf.j2ep.rules;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * A rule that will check the start of the URI for a specifed starting
 * directory/directories. If the directory is at the start this rule matches.
 * The process method will then remove this directory from the URI, making it
 * easy to map various servers to directory structures. If one needs some more
 * advanced types of rewriting use the RewriteRule.
 *
 * @author Anders Nyman
 */
public class DirectoryRule extends BaseRule {

    /**
     * The directory structure.
     */
    private String directory;

    private static final Logger logger = Logger.getLogger(DirectoryRule.class.getName());

    /**
     * Sets the directory structure that will be mapped to a specified server.
     *
     * @param directory The directory string
     */
    public void setDirectory(String directory) {
        if (directory == null) {
            throw new IllegalArgumentException(
                    "The directory string cannot be null.");
        } else {
            if (!directory.startsWith("/")) {
                directory = "/" + directory;
            }
            if (!directory.endsWith(("/"))) {
                directory += "/";
            }
            this.directory = directory;
        }
    }

    /**
     * Returns the directory structure that this rule will match on.
     *
     * @return The directory string
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Will see if the directory for the incoming URI is the same as this rule
     * is set to match on.
     *
     * @param request
     * @return
     * @see
     * net.sf.j2ep.model.Rule#matches(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        String uri = request.getServletPath();
        return (uri.startsWith(directory.substring(0, directory.length() - 1))); //@todo changed to match directory w/o trailing slash
    }

    /**
     * Removes the specified mapping directory from the URI.
     *
     * @param uri
     * @return
     * @see net.sf.j2ep.model.Rule#process(java.lang.String)
     */
    @Override
    public String process(String uri) {
        return uri.substring(directory.length() - 1);
    }

    /**
     * Does the opposite of process. revert(String URI) will add the directory
     * specified to the start of the incoming URI.
     *
     * @param uri
     * @return
     * @see net.sf.j2ep.model.Rule#revert(java.lang.String)
     */
    @Override
    public String revert(String uri) {
        if (uri.startsWith(directory)) {
            logger.log(Level.WARNING, " *** redundant path ****");
            return uri;
        } else if (uri.startsWith("/")) {
            return directory + uri.substring(1);
        } else {
            return uri;
        }
    }

}
