package org.irri.iric.portal;

import java.util.HashMap;

import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Textbox;

import com.google.api.services.oauth2.model.Userinfo;

public class DataLogins {

	public static HashMap<String, User> users = new HashMap<String, User>() {
		{
			
			put("user1", new User("user1", 1, "user1@gmail.com", null, "i06cmrskqM"));
			put("user2", new User("user2", 1, "user2@gmail.com", null, "YD62wuBylI"));
			put("admin", new User("admin", 2, "admin@gmail.com", null, "68TUd9SsOv"));

		}
	};

	public HashMap<String, User> getUsers() {
		return users;
	}

	public static User getUser(Textbox txtbox_username, Textbox txtbox_password) {

		User user = users.get(txtbox_username.getValue());

		if (user == null) {
			Notification.show("Username does not exist", Notification.TYPE_ERROR, txtbox_username, "after_start", 5000,
					false);
			return null;
		}

		if (!txtbox_password.getValue().equals(user.getPassword())) {
			Notification.show("Password does not match..", Notification.TYPE_ERROR, txtbox_password, "after_start",
					5000, false);
			return null;
		}

		return user;
	}

}
