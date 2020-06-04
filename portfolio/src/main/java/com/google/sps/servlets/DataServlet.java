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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;


@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private String name;
  private String category;
  private String content;
  private String comment;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Recommendation").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    
    //Number limit for entities loaded
    int maxNumber = results.countEntities();
    try {
      maxNumber = Integer.parseInt(request.getParameter("max"));
    } catch (NumberFormatException e) {}
    
    List<Entity> resultsList = results.asList(FetchOptions.Builder.withLimit(maxNumber));
    List<String> myToDos = new ArrayList<>(); 

    for (Entity entity : resultsList) {
      name = (String) entity.getProperty("name");
      category = (String) entity.getProperty("category");
      content = (String) entity.getProperty("content");
      comment = (String) entity.getProperty("comment");
      String putTogether = name + "'s " + category + " recommendation: " + content+". ";
      putTogether += name + " commented, \"" + comment + "\"";
      myToDos.add(putTogether);
    }

    String jsonResponse = convertToJson(myToDos);
    response.setContentType("application/json");
    response.getWriter().println(jsonResponse);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    name = request.getParameter("name");
    category = request.getParameter("category");
    content = request.getParameter("recommendation");
    comment = request.getParameter("comments");

    Entity recommendation = new Entity("Recommendation");
    recommendation.setProperty("name", name);
    recommendation.setProperty("category", category);
    recommendation.setProperty("content", content);
    recommendation.setProperty("comment", comment);
    recommendation.setProperty("timestamp", System.currentTimeMillis());

    datastore.put(recommendation);
    response.sendRedirect("/index.html#to-do");
  }

  private static String convertToJson(List<String> messages) { 
    Gson gson = new Gson();
    String jsonString = gson.toJson(messages);
    return jsonString;
  }
}
