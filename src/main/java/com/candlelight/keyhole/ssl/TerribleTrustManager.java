package com.candlelight.keyhole.ssl;

import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;

/**
 * A temporary hack until I can build the GUI to manage 3rd party certificates.
 */
public class TerribleTrustManager implements TrustManager {

    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
    }

    public void checkServerTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
    }
}
