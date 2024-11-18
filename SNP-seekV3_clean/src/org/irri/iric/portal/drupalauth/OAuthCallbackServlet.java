package org.irri.iric.portal.drupalauth;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.irri.iric.portal.zk.CookieController;
import org.json.JSONObject;  // Import the JSON library

import java.util.Map;
import java.util.HashMap;

public class OAuthCallbackServlet extends HttpServlet {
    
    private static final String CLIENT_ID = "java-app-client-id";
    private static final String CLIENT_SECRET = "***REMOVED***"; 
    private static final String REDIRECT_URI = "http://localhost:8085/SNP-seekV3_clean/callback";
    private static final String DRUPAL_TOKEN_URL = "https://snpseek-drupal.ddev.site/oauth/token";
    private static final String DRUPAL_USER_INFO_URL = "https://snpseek-drupal.ddev.site/oauth/userinfo"; 

    // Step 2: Handle the OAuth callback with the authorization code
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the authorization code and user ID from the callback
        String authorizationCode = request.getParameter("code");
        String userId = request.getParameter("user_id"); // Extract user ID from request parameters
        
        System.out.println("Received authorization code: " + authorizationCode);
        System.out.println("User ID: " + userId); // Log the user ID for debugging

        if (authorizationCode != null && userId != null) {
            // Step 3: Exchange the authorization code for an access token
            String accessToken = exchangeAuthorizationCodeForToken(authorizationCode, userId); // Pass user ID to the exchange method

            if (accessToken != null) {
                // Step 4: Use the access token to fetch user info from Drupal
                JSONObject userInfo = fetchUserInfo(accessToken);

                if (userInfo != null) {
                    // Step 5: Log the user in by creating a session in the Java app
                    System.out.println("User info fetched: " + userInfo.toString());
                    loginUser(userInfo, request, response);
                } else {
                    response.getWriter().println("Error: Unable to fetch user info.");
                }
            } else {
                response.getWriter().println("Error: Unable to exchange authorization code for token.");
            }
        } else {
            response.getWriter().println("No authorization code or user ID received.");
        }
    }

    // Step 3: Exchange the authorization code for an access token
    private String exchangeAuthorizationCodeForToken(String authorizationCode, String userId) throws IOException {
        String requestBody = "grant_type=authorization_code"
                + "&code=" + URLEncoder.encode(authorizationCode, "UTF-8")
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
                + "&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET
                + "&user_id=" + URLEncoder.encode(userId, "UTF-8"); // Include user ID in the request

        // Log the request details
        System.out.println("Exchanging Authorization Code for Token");
        System.out.println("Request Body: " + requestBody); // Log request body

        URL url = new URL(DRUPAL_TOKEN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Send the request body
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        // Check the HTTP response code
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Parse the response if it’s successful
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder responseBody = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseBody.append(inputLine);
                }
                JSONObject jsonResponse = new JSONObject(responseBody.toString());
                return jsonResponse.getString("access_token");
            }
        } else {
            // Log error details
            try (BufferedReader errorStream = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorStream.readLine()) != null) {
                    errorResponse.append(line);
                }
                System.err.println("Token exchange failed. Response code: " + responseCode);
                System.err.println("Error response: " + errorResponse);
            }
            return null;
        }
    }

    // Step 4: Use the access token to fetch the user profile from Drupal
    private JSONObject fetchUserInfo(String accessToken) throws IOException {
        URL url = new URL(DRUPAL_USER_INFO_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        // Check if the response is successful
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder responseBody = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
            in.close();

            return new JSONObject(responseBody.toString()); // Assuming the user info is returned as JSON
        } else {
            System.err.println("Failed to fetch user info. HTTP response code: " + responseCode);
            return null;
        }
    }

    // Step 5: Log the user in to the Java app by creating a session
    private void loginUser(JSONObject userInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        CookieController cookieController = new CookieController();
        cookieController.logCurrentCookies(request);
        // Create a user info map to store multiple pieces of user data
        Map<String, Object> userInfoMap = new HashMap<>();

        // Assuming the JSON contains a 'username' and 'email'
        if (userInfo.has("username") && userInfo.has("email")) {
            userInfoMap.put("username", userInfo.getString("username")); // Store the username
            userInfoMap.put("email", userInfo.getString("email"));       // Store the email

            // Save the user info map into the session
            session.setAttribute("userInfo", userInfoMap);

            // DEBUG PRINT: Check if session has the correct user info
            System.out.println("User logged in: " + userInfo.getString("username"));
        } else {
            System.err.println("User info response is missing 'username' or 'email'.");
        }

        // Redirect to the home page or dashboard after successful login
        response.sendRedirect("index.zul");
    }
}
