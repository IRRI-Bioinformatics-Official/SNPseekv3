package org.irri.iric.portal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/oauth/callback")
public class OAuthCallbackServlet extends HttpServlet {

    private static final String CLIENT_ID = "***REMOVED***"; 
    private static final String CLIENT_SECRET = "rheana";
    private static final String REDIRECT_URI = "http://localhost:8085/SNP-seekV3_clean/oauth/callback"; 
    private static final String TOKEN_URL = "http://localhost:8080/oauth/token";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Callback is Working");
        String authorizationCode = request.getParameter("code");
        System.out.println("Authorization Code Received: " + authorizationCode);

        if (authorizationCode != null && !authorizationCode.isEmpty()) {
            try {
                // Exchange the authorization code for an access token
                String accessToken = generateAccessToken(authorizationCode);

                // Use HttpServletRequest to get or create the session
                javax.servlet.http.HttpSession httpSession = request.getSession(true);  // true = create if not exists

                // Store access token in the session
                httpSession.setAttribute("access_token", accessToken);

                // Log for debugging purposes
                System.out.println("Access token stored in session: " + accessToken);
                System.out.println("OAuth callback session ID: " + httpSession.getId());

                // Redirect to the secured area or dashboard
                response.sendRedirect("/SNP-seekV3_clean/index.zul");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth Callback Error");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing authorization code");
        }
    }

    private String generateAccessToken(String authorizationCode) throws IOException, URISyntaxException {
        URI uri = new URI(TOKEN_URL);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String params = "grant_type=authorization_code" +
                        "&code=" + authorizationCode +
                        "&redirect_uri=" + REDIRECT_URI +
                        "&client_id=" + CLIENT_ID +
                        "&client_secret=" + CLIENT_SECRET;

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = params.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = in.readLine()) != null) {
            response.append(responseLine.trim());
        }
        System.out.println("Response Code: " + responseCode);
        System.out.println("Response: " + response.toString());

        if (responseCode == HttpURLConnection.HTTP_OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(response.toString());
            return responseJson.get("access_token").asText();
        } else {
            throw new IOException("Failed to obtain access token, response code: " + responseCode + ", response: " + response.toString());
        }
    }
}
