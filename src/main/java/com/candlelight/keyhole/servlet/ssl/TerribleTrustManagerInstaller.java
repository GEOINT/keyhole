package com.candlelight.keyhole.servlet.ssl;

import com.candlelight.keyhole.ssl.TerribleTrustManager;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 */
@WebListener("terribleTrustManager")
public class TerribleTrustManagerInstaller implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(TerribleTrustManagerInstaller.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.log(Level.WARNING, "This proxy currently disables ssl server"
                + " valiation.  This will be fixed in a future version.");
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TerribleTrustManager()},
                    new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
