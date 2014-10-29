package com.candlelight.keyhole.servlet.ssl;

import com.candlelight.keyhole.ssl.TerribleTrustManager;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.jsslutils.extra.apachehttpclient.SslContextedSecureProtocolSocketFactory;

/**
 *
 */
@WebListener("terribleTrustManager")
public class TerribleTrustManagerInstaller implements ServletContextListener {

    private static final Logger logger = Logger
            .getLogger(TerribleTrustManagerInstaller.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.log(Level.WARNING, "This proxy currently disables ssl server"
                + " valiation.  This will be fixed in a future version.");

        try {
            //register it with the Java SSLContext
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TerribleTrustManager()},
                    new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            //also register it with the Apache httpclient (sigh)
            SslContextedSecureProtocolSocketFactory secureProtocolSocketFactory
                    = new SslContextedSecureProtocolSocketFactory(sc);
            secureProtocolSocketFactory.setHostnameVerification(false);
            Protocol.registerProtocol("https", new Protocol("https",
                    (ProtocolSocketFactory) secureProtocolSocketFactory, 443));
        } catch (GeneralSecurityException e) {
            logger.log(Level.SEVERE, "Unable to register SSL work around, "
                    + "requsts to HTTPS servers with invalid certificates will "
                    + "not work.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
