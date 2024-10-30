package org.irri.iric.portal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import user.ui.module.util.constants.SessionConstants;
import user.ui.module.util.constants.UserConstants;
import org.springframework.stereotype.Controller;

public class LoginController extends SelectorComposer<Window> {

	@Wire
	private Button btn_login;

	@Wire
	private Div loginfo;

	@Wire
	private Div logform;

	@Wire
	private Label lbl_id;

	@Wire
	private Textbox txtbox_username;

	@Wire
	private Textbox txtbox_password;

	@Wire
	private Label lbl_email;

	@Wire
	private Label lbl_firstname;

	@Wire
	private Label lbl_lastname;

	@Wire
	private Label lbl_name;
	
	@Wire
	private Label dsNumber;

	@Wire
	private Image image_Id;

	private User user;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		
//		if (user != null) {
//			logform.setVisible(false);
//			loginfo.setVisible(true);
//			user.getUserInfo();
//			dsNumber.setValue("7");
//
//			if (user.getUserInfo() != null) {
//				lbl_id.setValue(user.getUserInfo().getId());
//				lbl_email.setValue(user.getUserInfo().getEmail());
//				lbl_name.setValue(user.getUserInfo().getName());
//				lbl_firstname.setValue(user.getUserInfo().getGivenName());
//				lbl_lastname.setValue(user.getUserInfo().getFamilyName());
//				image_Id.setSrc(user.getUserInfo().getPicture());
//			} else {
//				lbl_id.setValue("");
//				lbl_email.setValue(user.getEmail());
//				lbl_name.setValue(user.getUsername());
//				lbl_firstname.setValue("");
//				lbl_lastname.setValue("");
//				image_Id.setSrc("");
//			}
//
//		} else {
//			logform.setVisible(true);
//			dsNumber.setValue("1");
//			loginfo.setVisible(false);
//		}

	}

	@Listen("onClick=#btn_login")
	public void queryVariants() {

		User user = DataLogins.getUser(txtbox_username, txtbox_password);

		if (user != null) {
			Session sess = Sessions.getCurrent();
			User userCred = new User();
			userCred.setUsername(user.getUsername());
			userCred.setRoleId(user.getRoleId());

			Notification.show("Login");

			String cm;

			if (user.getRoleId() == 1)
				cm = "/" + UserConstants.USER + ".properties";
			else
				cm = "/" + UserConstants.ADMIN + ".properties";

			Properties contentProp = new Properties();
			InputStream contentManager = AppContext.class.getResourceAsStream(cm);
			try {

				contentProp.load(contentManager);

			} catch (IOException e) {

				e.printStackTrace();
			}

			// AppContext.setUserContent();

			sess.setAttribute(SessionConstants.USER_CREDENTIAL, userCred);
			sess.setAttribute(SessionConstants.CONTENT_MANAGER, contentProp);

			Executions.sendRedirect("index.zul");
		}

	}

	@Listen("onClick=#btn_oauth2_login")
    public void oauth2Login() {
        // Redirect the user to the OAuthServlet (starts OAuth2 login flow with Drupal)
        Executions.sendRedirect("/OAuthServlet");
    }
    
    @Listen("onClick=#btn_logout")
    public void logout() {
    	Session sess = Sessions.getCurrent();
        
        if (sess != null) {
            // Log the current user info before clearing
            Map<String, Object> userInfo = (Map<String, Object>) sess.getAttribute("userInfo");
            System.out.println("User info before logout: " + userInfo);
            
            // Remove user info and invalidate session
            sess.removeAttribute("userInfo");
            sess.invalidate();
            System.out.println("Session invalidated. User info should be cleared.");
        }
        
        Executions.sendRedirect("index.zul"); // Redirect after logout
    }

}
