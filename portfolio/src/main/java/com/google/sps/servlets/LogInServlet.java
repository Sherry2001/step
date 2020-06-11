package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/checklogin")
public class LogInServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService(); 
    response.setContentType("text/html");
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL("/index.html#to-do");
      response.getWriter().println("<p>Hi Sherry, are you sure you want to delete?</p>");
      response.getWriter().println("<button onclick=\"deleteData()\">Delete All</button>");
      response.getWriter().println("<a href=\"" + logoutUrl + "\">Log Out</a>");

    } else {
      String loginUrl = userService.createLoginURL("/index.html#to-do");
      response.getWriter().println("<p>You can only do this if you are Sherry. Log in, Sherry, and try again</p>");
      response.getWriter().println("<a href=\"" + loginUrl + "\">Log in!</a>"); 
    }
  }
}