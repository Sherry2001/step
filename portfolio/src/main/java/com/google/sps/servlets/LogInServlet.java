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
import java.util.List;
import java.util.Arrays;

@WebServlet("/checklogin")
public class LogInServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService(); 
    response.setContentType("application/json");

    boolean loggedIn;
    String email = null;
    String url;
    boolean access = false;
    
    List<String> acceptedEmails = Arrays.asList("shershi@google.com","ricazhang@google.com","alfredh@google.com");

    if (userService.isUserLoggedIn()) {
      loggedIn = true;
      email = userService.getCurrentUser().getEmail();
      if (acceptedEmails.contains(email)) {
        access = true;
      }
      String logoutUrl = userService.createLogoutURL("/index.html#to-do");
      url = logoutUrl;
    } else {
      loggedIn = false;
      String loginUrl = userService.createLoginURL("/index.html#to-do");
      url = loginUrl;
    }

    CheckLoginResponse checkLoginResponse = new CheckLoginResponse(loggedIn, url, email, access);

    //Convert to JSON string
    Gson gson = new Gson();
    String responseJson = gson.toJson(checkLoginResponse);
    response.getWriter().println(responseJson);
  }
}
