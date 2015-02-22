package org.geoint.keyhole.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

public class AuthenticationRequestListener implements ServletRequestListener {

    private static final Logger filterLog
            = Logger.getLogger(AuthenticationRequestListener.class.getName());

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request
                = (HttpServletRequest) sre.getServletRequest();

        ArrayList<String> headerNames
                = Collections.list(request.getHeaderNames());

        headerNames.stream().forEach((name) -> {
            System.out.println(name + " : " + request.getHeader(name));
        });

    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest request
                = (HttpServletRequest) sre.getServletRequest();

        ArrayList<String> headerNames
                = Collections.list(request.getHeaderNames());

        headerNames.stream().forEach((name) -> {
            System.out.println(name + " : " + request.getHeader(name));
        });

    }

}
