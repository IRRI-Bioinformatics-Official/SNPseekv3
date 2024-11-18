package org.irri.iric.portal.drupalauth;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logout-drupal")
public class DrupalLogoutServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Invalidate the Java session to log out from the Java application
        request.getSession().invalidate();

        // Define the Drupal logout URL with a destination parameter to redirect back to your Java application
        String drupalLogoutUrl = "https://snpseek-drupal.ddev.site/custom-logout?destination=" + "http://localhost:8085/SNP-seekV3_clean/index.zul";;
        
        // Redirect the user to the Drupal logout URL
        response.sendRedirect(drupalLogoutUrl);
    }
}
