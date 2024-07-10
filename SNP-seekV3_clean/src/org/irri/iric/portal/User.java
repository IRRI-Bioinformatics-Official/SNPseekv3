package org.irri.iric.portal;

import com.google.api.services.oauth2.model.Userinfo;

public class User {

	private String username;

	private String password;

	private String email;

	private int roleId;

	private Userinfo userInfo;

	public User() {

	}

	public User(String username, int roleid, String email, Userinfo userinfo, String password) {
		this.username = username;
		this.password = password;
		this.roleId = roleid;
		this.userInfo = userinfo;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public Userinfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(Userinfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
