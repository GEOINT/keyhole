package org.geoint.keyhole.ssl;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.net.ssl.TrustManager;

/**
 * A temporary hack until I can build the GUI to manage 3rd party certificates.
 */
public class TerribleTrustManager implements TrustManager {

    private static final Logger logger = Logger
            .getLogger(TerribleTrustManager.class.getName());

    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(
            X509Certificate[] certs, String authType) {
        logger.log(Level.FINE, "The following client certs were approved by "
                + "the TerribleTrustManager: {0}", certString(certs));
    }

    public void checkServerTrusted(
            X509Certificate[] certs, String authType) {
        logger.log(Level.FINE, "The following server certs were approved by "
                + "the TerribleTrustManager: {0}", certString(certs));
    }

    private String certString(X509Certificate[] certs) {
        return Arrays.stream(certs).map(
                (c) -> c.getSubjectDN().getName())
                .collect(Collectors.joining(","));
    }
}
