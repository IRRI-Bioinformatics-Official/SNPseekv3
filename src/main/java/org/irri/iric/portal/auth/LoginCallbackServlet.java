package org.irri.iric.portal.auth;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.irri.iric.portal.AppContext;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.GenericUrl;

@WebServlet("/login-callback")
public class LoginCallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return OAuthUtils.newFlow();
  }

  @Override
  protected String getRedirectUri(HttpServletRequest request) {
    GenericUrl url = new GenericUrl(request.getRequestURL().toString());
    url.setRawPath(request.getContextPath() + "/login-callback");
    return url.build();
  }

  @Override
  protected String getUserId(HttpServletRequest request) {
    return request.getSession().getId();
  }

  @Override
  protected void onSuccess(HttpServletRequest request, HttpServletResponse response, Credential credential)
      throws IOException {
    response.sendRedirect(AppContext.getHostname() + "/profile");
  }

  @Override
  protected void onError(
      HttpServletRequest request, HttpServletResponse response, AuthorizationCodeResponseUrl errorResponse)
      throws IOException {
    response.getWriter().print("Login cancelled.");
  }
}