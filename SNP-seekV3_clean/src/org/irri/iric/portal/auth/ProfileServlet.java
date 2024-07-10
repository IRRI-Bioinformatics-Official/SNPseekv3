package org.irri.iric.portal.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.PropertyConstants;
import org.irri.iric.portal.User;
import org.zkoss.zk.ui.Executions;

import com.google.api.services.oauth2.model.Userinfo;

import user.ui.module.util.constants.SessionConstants;
import user.ui.module.util.constants.UserConstants;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;");
		response.getWriter().println("<h1>Redireting...</h1>");

		String sessionId = request.getSession().getId();
		boolean isUserLoggedIn = OAuthUtils.isUserLoggedIn(sessionId);

		if (isUserLoggedIn) {
			Userinfo userInfo = OAuthUtils.getUserInfo(sessionId);

			HttpSession sess = request.getSession();

			User userCred = new User();
			userCred.setUsername(userInfo.getEmail());
			userCred.setRoleId(1);
			userCred.setUserInfo(userInfo);

			String[] email = userInfo.getEmail().trim().split("@");

			String cm = "";
			if (email[1].equals("irri.org"))
				cm = "/" + UserConstants.ADMIN + ".properties";
			else
				cm = "/" + UserConstants.USER + ".properties";

			Properties contentProp = new Properties();
			InputStream contentManager = AppContext.class.getResourceAsStream(cm);
			try {

				contentProp.load(contentManager);

			} catch (IOException e) {

				e.printStackTrace();
			}

			sess.setAttribute(SessionConstants.USER_CREDENTIAL, userCred);
			sess.setAttribute(SessionConstants.CONTENT_MANAGER, contentProp);

			response.getWriter().println("<p>ID: " + userInfo.getId() + "</p>");
			response.getWriter().println("<p>Email: " + userInfo.getEmail() + "</p>");
			response.getWriter().println("<p>First name: " + userInfo.getGivenName() + "</p>");
			response.getWriter().println("<p>Last name: " + userInfo.getFamilyName() + "</p>");
			response.getWriter().println("<p>Full name: " + userInfo.getName() + "</p>");
			response.getWriter().println("<img src=\"" + userInfo.getPicture() + "\" />");

			response.getWriter().println("<p><a href=\"/logout\">Logout</a></p>");

			response.sendRedirect("/index.zul");

		} else {
			response.getWriter().println("<a href=\"/login\">Login with Google</a>");
		}
	}
}