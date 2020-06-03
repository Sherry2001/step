// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> messages;
  
  @Override 
  public void init() {
    messages = new ArrayList<String>(); 
    messages.add("Hello, this is my first comment");
    messages.add("Hello, this is my second comment");
    messages.add("Hi, this is my third comment");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String jsonResponse = convertToJson(messages);
    response.setContentType("application/json");
    response.getWriter().println(jsonResponse);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String category = request.getParameter("category");
    String recommendation = request.getParameter("recommendation");
    String comments = request.getParameter("comments");

    String putTogether = name + "'s " + category + " recommendation: " + recommendation;
    putTogether += name + " commented, \"" + comments + "\"";

    messages.add(putTogether); 
    System.out.println("got here" + putTogether);
    response.sendRedirect("/index.html#to-do");
  }

  private String convertToJson(List<String> messages) { 
    Gson gson = new Gson();
    String jsonString = gson.toJson(messages);
    return jsonString;
  }
}
