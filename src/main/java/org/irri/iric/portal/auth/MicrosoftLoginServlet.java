package org.irri.iric.portal.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.irri.iric.portal.AppContext;

@WebServlet("/microsoftLogin")
public class MicrosoftLoginServlet extends HttpServlet {
	// Load sensitive config from environment variables or servlet context init params
	private static final String CLIENT_ID_ENV = "MICROSOFT_CLIENT_ID";
	private static final String TENANT_ID_ENV = "MICROSOFT_TENANT_ID";
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        String redirect_URI = AppContext.getHostname()+"/callback";
        
        // Prefer environment variables; fall back to servlet context init parameters
        String clientId = System.getenv(CLIENT_ID_ENV);
        AppContext.debug(CLIENT_ID_ENV + ":" + clientId);
        if (clientId == null) {
            clientId = getServletContext().getInitParameter("MICROSOFT_CLIENT");
        }
        String tenantId = System.getenv(TENANT_ID_ENV);
        AppContext.debug(TENANT_ID_ENV + ":" + tenantId);
        if (tenantId == null) {
            tenantId = getServletContext().getInitParameter("MICROSOFT_TENANT_ID");
        }
        
        // If required configuration is missing, return a clear error (do not expose the values)
        if (clientId == null || tenantId == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<head><title>Configuration error</title></head>");
            out.println("<body>");
            out.println("<h2>Server configuration error</h2>");
            out.println("<p>Microsoft OAuth configuration is missing. Please set environment variables " + CLIENT_ID_ENV + ", " + TENANT_ID_ENV + ", and " + redirect_URI + " or provide servlet context init parameters.</p>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        // Build the Microsoft OAuth2 authorize URL
//        String authUrl = "https://login.microsoftonline.com/" + URLEncoder.encode(tenantId, "UTF-8") + "/oauth2/v2.0/authorize?"
//                + "client_id=" + URLEncoder.encode(clientId, "UTF-8")
//                + "&response_type=code"
//                + "&redirect_uri=" + URLEncoder.encode(redirect_URI, "UTF-8")
//                + "&scope=" + URLEncoder.encode("User.Read", "UTF-8");
     // FIXED
//        String authUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/authorize?"
//				+ "client_id=" + clientId
//				+ "&response_type=code"
//				+ "&redirect_uri=" + URLEncoder.encode(redirect_URI, "UTF-8")
//				+ "&scope=" + URLEncoder.encode("User.Read", "UTF-8");
        
        String authUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/authorize?"
                + "client_id=" + URLEncoder.encode(clientId, "UTF-8")
                + "&response_type=code"
                + "&redirect_uri=" + URLEncoder.encode(redirect_URI, "UTF-8")
                + "&scope=" + URLEncoder.encode("User.Read", "UTF-8")
                + "&domain_hint=" + URLEncoder.encode("cgiar.org", "UTF-8");  // Add this line
        

        // Generate the HTML login page with a button that redirects to Microsoft's OAuth login
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Microsoft Login</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f4; margin: 0; }");
        out.println(".login-container { background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center; }");
        out.println("h2 { margin-bottom: 20px; }");
        out.println(".login-btn { padding: 10px 20px; background-color: #0078d4; color: white; font-size: 16px; border: none; border-radius: 5px; cursor: pointer; }");
        out.println(".login-btn:hover { background-color: #005a9e; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='login-container'>");
        out.println("<h2>Sign in with Microsoft</h2>");
        out.println("<a href='" + authUrl + "'>");
        out.println("<button class='login-btn'>Login with Microsoft</button>");
        out.println("</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        
        response.sendRedirect(authUrl);
        
        // NOTE: previously the servlet also called response.sendRedirect(authUrl) here which caused
        // the HTML to be ignored and an immediate redirect. We intentionally do NOT auto-redirect so
        // the user sees the login button and any configuration error messages.
    }
    
   
}