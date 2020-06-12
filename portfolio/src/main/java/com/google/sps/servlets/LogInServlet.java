package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.CheckLoginResponse;
import com.google.gson.Gson;

@WebServlet("/checklogin")
public class LogInServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService(); 
    response.setContentType("application/json");

    Boolean loggedIn;
    String email = null;
    String url;

    if (userService.isUserLoggedIn()) {
      loggedIn = true;
      email = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL("/index.html#to-do");
      url = logoutUrl;
    } else {
      loggedIn = false;
      String loginUrl = userService.createLoginURL("/index.html#to-do");
      url = loginUrl;
    }

    CheckLoginResponse checkLoginResponse = new CheckLoginResponse(loggedIn, url, email);

    //Convert to JSON string
    Gson gson = new Gson();
    String responseJson = gson.toJson(checkLoginResponse);
    response.getWriter().println(responseJson);
  }
}
