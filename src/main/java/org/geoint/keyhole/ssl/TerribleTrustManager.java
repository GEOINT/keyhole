package org.geoint.keyhole.ssl;

import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;

/**
 * A temporary hack until I can build the GUI to manage 3rd party certificates.
 */
public class TerribleTrustManager implements X509TrustManager {

    private static final Logger logger = Logger
            .getLogger(TerribleTrustManager.class.getName());

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(
            X509Certificate[] certs, String authType) {
        System.out.println("------------ttr client cert authType: " + authType + "------------------");
        logger.log(Level.FINE, "The following client certs were approved by "
                + "the TerribleTrustManager: {0}", certString(certs));
    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] certs, String authType) {
        System.out.println("------------ttr server cert authType: " + authType + "------------------");
        logger.log(Level.FINE, "The following server certs were approved by "
                + "the TerribleTrustManager: {0}", certString(certs));
    }

    private String certString(X509Certificate[] certs) {
        if (certs == null) {
            return "";
        }
        
        StringBuilder certString = new StringBuilder();
        for (X509Certificate cert : certs) {
            certString.append(cert.getSubjectDN().getName()).append(",");            
        }
//        return Arrays.stream(certs).map(
//                (c) -> c.getSubjectDN().getName())
//                .collect(Collectors.joining(","));
        return certString.toString();
    }
}
