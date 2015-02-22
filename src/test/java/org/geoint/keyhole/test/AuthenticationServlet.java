package org.geoint.keyhole.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 *
 */
public class AuthenticationServlet extends HttpServlet {

    private static final Logger logger
            = Logger.getLogger(AuthenticationServlet.class.getName());
    private ServletConfig config = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param config
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public AuthenticationServlet(ServletConfig config) throws Exception {
        this.config = config;
        super.init(this.config);
    }

    public AuthenticationServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {

        logger.log(Level.INFO, "* AuthenticationServlet initialized");

    }

    
    /**
     * Handles the HTTP <code>GET</code> method.
     * Returns basic http 200 'ok' response code
     * 
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        logger.log(Level.INFO, "AuthenticationServlet#doGet() called");
        response.setStatus(HttpServletResponse.SC_OK);

    }

    /**
     * Handles the HTTP <code>POST</code> method.  
     * Returns mock credentials in the response header that have been
     * base64 encoded.  
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        logger.log(Level.INFO, "AuthenticationServlet#doPost() called");
        String credentials = "guest:guest123";

        //set authorization header on the response
        String authVal = Base64.getEncoder()
                .encodeToString(credentials.getBytes("utf-8"));
        response.setHeader("Authorization", authVal);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Authentication Servlet";
    }// </editor-fold>

}
