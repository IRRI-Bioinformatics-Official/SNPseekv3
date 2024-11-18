package org.irri.iric.portal.drupalauth;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthServlet extends HttpServlet {
    
    // OAuth 2.0 credentials and endpoint URLs
    private static final String CLIENT_ID = "java-app-client-id";
    private static final String REDIRECT_URI = "http://localhost:8085/SNP-seekV3_clean/callback";
    private static final String DRUPAL_AUTHORIZE_URL = "https://snpseek-drupal.ddev.site/oauth/authorize";
    
    // Step 1: Redirect to the Drupal OAuth 2.0 Authorization Endpoint
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorizeUrl = DRUPAL_AUTHORIZE_URL
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
                + "&response_type=code"; 

        // Redirect user to the authorization endpoint
        response.sendRedirect(authorizeUrl);
    }
}