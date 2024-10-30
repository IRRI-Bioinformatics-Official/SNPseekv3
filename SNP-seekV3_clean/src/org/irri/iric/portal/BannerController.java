package org.irri.iric.portal;

import org.irri.iric.portal.zk.CookieController;
import org.irri.iric.portal.zk.SessionController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;

import java.util.Map;

public class BannerController extends SelectorComposer<Component> {

    @Wire
    private Menu menuLogout;

    @Wire
    private Menuitem mi_login;

    private SessionController sessionController = new SessionController();

    private Session sess;
    private User user;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        sess = Sessions.getCurrent();
        user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

        if (user != null) {
            // Display the username from the User object
            menuLogout.setLabel("Logged in as: " + user.getUsername());
            menuLogout.setVisible(true);
            mi_login.setVisible(false);
        } else {
            // Check for OAuth user info
            Map<String, Object> userInfo = (Map<String, Object>) sess.getAttribute("userInfo");
            if (userInfo != null && userInfo.get("username") != null) {
                // Display the username from the OAuth session
                menuLogout.setLabel("Logged in via OAuth2: " + userInfo.get("username"));
                menuLogout.setVisible(true);
                mi_login.setVisible(false);
            } else {
                menuLogout.setVisible(false);
                mi_login.setVisible(true);
            }
        }
    }

    @Listen("onClick=#menuLogout")
    public void logout() {
        if (sess != null) {
            // Log the current user info before clearing
            Map<String, Object> userInfo = (Map<String, Object>) sess.getAttribute("userInfo");
            System.out.println("User info before logout: " + userInfo);

            // Remove user info and invalidate session
            sess.removeAttribute("userInfo");
            sess.invalidate();
            System.out.println("Session invalidated. User info should be cleared.");
        }

        clearCookies(); // Ensure cookies are cleared
        Executions.sendRedirect("index.zul"); // Redirect after logout
    }

    private void clearCookies() {
        HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        HttpServletResponse response = (HttpServletResponse) Executions.getCurrent().getNativeResponse();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Set the cookie's max age to 0 to delete it
                cookie.setMaxAge(0);
                cookie.setValue(null); // Clear the cookie value
                cookie.setPath("/"); // Ensure you're deleting it from the correct path
                response.addCookie(cookie); // Add the cookie to the response
                System.out.println("Cleared cookie: " + cookie.getName());
            }
        } else {
            System.out.println("No cookies found to clear.");
        }
    }

    @Listen("onClick=#mi_login")
    public void login() {
        // Redirect to the login page (assuming you have a dedicated login page)
        Executions.sendRedirect("login.zul");
    }
}
