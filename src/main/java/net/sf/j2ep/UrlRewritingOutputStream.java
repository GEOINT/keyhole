/*
 * Copyright 2000,2004 The Apache Software Foundation.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import net.sf.j2ep.model.Server;

import java.util.logging.Logger;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper for the default output stream. This class will make sure all data
 * being sent is cached by the stream and can rewrite all the links.
 *
 * @author Anders Nyman
 *
 */
public final class UrlRewritingOutputStream extends ServletOutputStream {

    /**
     * The stream we are wrapping, is the original response stream.
     */
    private final ServletOutputStream originalStream;

    /**
     * The character encoding of the response stream, e.g. UTF-8.
     */
    private final String encoding;

    /**
     * Stream that is written to, works as a buffer for the response stream.
     */
    private final ByteArrayOutputStream stream;

    /**
     * The server, needed when we rewrite absolute links.
     */
    private final String ownHostName;

    /**
     * The contextPath, needed when we rewrite links.
     */
    private final String contextPath;

    /**
     * The servers.
     */
    private final ServerChain serverChain;

    /**
     * Regex matching links in the HTML.
     */
    private static final Pattern linkPattern
            = Pattern.compile("\\b(href=|src=|action=|url\\()([\"\'])(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);

    private static final Logger logger = Logger.getLogger("org.geoint.keyhole");

    /**
     * Basic constructor.
     *
     * @param originalStream The stream we are wrapping
     * @param encoding
     * @param serverChain
     * @param ownHostName
     * @param contextPath
     */
    public UrlRewritingOutputStream(ServletOutputStream originalStream,
            String encoding, String ownHostName, String contextPath,
            ServerChain serverChain) {
        this.originalStream = originalStream;
        this.encoding = encoding;
        this.ownHostName = ownHostName;
        this.contextPath = contextPath;
        this.serverChain = serverChain;

        stream = new ByteArrayOutputStream();
    }

    /**
     * @param b
     * @throws java.io.IOException
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        stream.write(b);
    }

    /**
     * @param b
     * @param len
     * @param off
     * @throws java.io.IOException
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }

    /**
     * @param b
     * @throws java.io.IOException
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    /**
     * Processes the stream looking for links, all links found are rewritten.
     * After this the stream is written to the response.
     *
     * @param server The server that we are using for this request.
     * @throws IOException Is thrown when there is a problem with the streams
     */
    public void rewrite(Server server) throws IOException {
        /*
         * Using regex can be quite harsh sometimes so here is how
         * the regex trying to find links works
         * 
         * \\b(href=|src=|action=|url:\\s*|url\\()([\"\'])
         * This part is the identification of links, matching
         * something like href=", href=' and href=
         * 
         * (([^/]+://)([^/<>]+))?
         * This is to identify absolute paths. A link doesn't have
         * to be absolute therefor there is a ?.
         * 
         * ([^\"\'>]*)
         * This is the link
         * 
         * [\"\']
         * Ending " or '
         * 
         * $1 - link type, e.g. href=
         * $2 - ", ' or whitespace
         * $3 - The entire http://www.server.com if present
         * $4 - The protocol, e.g http:// or ftp:// 
         * $5 - The host name, e.g. www.server.com
         * $6 - The link
         */
        StringBuffer page = new StringBuffer();

        Charset charset = Charset.forName(encoding);
        CharsetEncoder encoder = charset.newEncoder();
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer buf = encoder.encode(CharBuffer.wrap(stream.toString(encoding)));
        Matcher matcher = linkPattern.matcher(decoder.decode(buf));
//        Matcher matcher = linkPattern.matcher(stream.toString(encoding));
        while (matcher.find()) {

            String link = matcher.group(6).replaceAll("\\$", "\\\\\\$");
            if (link.length() == 0) {
                link = "/";
            }

            String rewritten = null;
            if (matcher.group(4) != null) {
                rewritten = handleExternalLink(matcher, link);
            } else if (link.startsWith("/")) {
                rewritten = handleLocalLink(server, matcher, link);
            }

            if (rewritten != null) {
                logger.finest("Found link " + link + " >> " + rewritten);
                matcher.appendReplacement(page, rewritten);
            }
        }

        matcher.appendTail(page);
        originalStream.print(page.toString());
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        if (writeListener != null) {
            try {
                writeListener.onWritePossible();
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Unable to notify write listener that we're ready", ex);
            }
        }
    }

    /**
     * Rewrites a absolute path starting with a protocol e.g.
     * http://www.server.com/index.html
     *
     * @param matcher The matcher used for this link
     * @param link The part of the link after the domain name
     * @return The link now rewritten
     */
    private String handleExternalLink(Matcher matcher, String link) {
        String location = matcher.group(5) + link;
        Server matchingServer = serverChain.getServerMapped(location);

        if (matchingServer != null) {
            link = link.substring(matchingServer.getPath().length());
            link = matchingServer.getRule().revert(link);
            String type = matcher.group(1);
            String separator = matcher.group(2);
            String protocol = matcher.group(4);
            return type + separator + protocol + ownHostName + contextPath + link + separator;
        } else {
            return null;
        }
    }

    /**
     *
     * @param server The current server we are using for this page
     * @param matcher The matcher used for this link
     * @param link The original link
     * @return The rewritten link
     */
    private String handleLocalLink(Server server, Matcher matcher, String link) {
        String serverDir = server.getPath();

        if (serverDir.equals("") || link.startsWith(serverDir + "/")) {
            link = server.getRule().revert(link.substring(serverDir.length()));
            String type = matcher.group(1);
            String separator = matcher.group(2);
            return type + separator + contextPath + link + separator;
        } else {
            return null;
        }
    }

    /**
     * @throws java.io.IOException
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        stream.close();
    }

}
