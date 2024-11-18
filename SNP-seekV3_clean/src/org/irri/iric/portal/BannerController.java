package org.irri.iric.portal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import user.ui.module.util.constants.SessionConstants;

public class BannerController extends SelectorComposer<Component> {

    @Wire
    private Menu menuLogout;

    @Wire
    private Menuitem mi_login;

    @Wire
    private Menuitem mi_logout;

    private Session zkSession;
    private HttpSession httpSession;
    private User user;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        // Initialize both session mechanisms
        zkSession = Sessions.getCurrent();
        user = (User) zkSession.getAttribute(SessionConstants.USER_CREDENTIAL);

        // Check and update the login status
        updateLoginStatus();
    }

    private void updateLoginStatus() {
        // Obtain HttpServletRequest and current HttpSession
        HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        httpSession = request.getSession(false); // Prevent creating a new session if it doesn't exist

        Map<String, Object> userInfo = null;
        if (httpSession != null) {
            userInfo = (Map<String, Object>) httpSession.getAttribute("userInfo");
        }

        // Handle login status based on both session mechanisms
        if (user != null) {
            // Old session mechanism: Display username from User object
            menuLogout.setLabel("Logged in as: " + user.getUsername());
            menuLogout.setVisible(true);
            mi_login.setVisible(false);
        } else if (userInfo != null && userInfo.get("username") != null) {
            // New session mechanism: Display username from userInfo map
            menuLogout.setLabel("Logged in via OAuth2: " + userInfo.get("username"));
            menuLogout.setVisible(true);
            mi_login.setVisible(false);
        } else {
            // No user is logged in
            menuLogout.setVisible(false);
            mi_login.setVisible(true);
        }
    }

    @Listen("onClick=#mi_login")
    public void login() {
        // Redirect to the login page
        Executions.sendRedirect("login.zul");
    }

    @Listen("onClick=#mi_logout")
    public void onLogoutButtonClick() {
        // Redirect to the LogoutServlet to handle session invalidation
        Executions.sendRedirect("/logout-drupal");
    }
}
