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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.sps.data.Recommendation;

@WebServlet("/data")
@MultipartConfig 
public class DataServlet extends HttpServlet {

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final String NAME = "name";
  private static final String CATEGORY = "category";
  private static final String CONTENT = "content";
  private static final String COMMENT = "comment"; 

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
    List<Recommendation> myToDos = new ArrayList<>(); 

    for (Entity entity : resultsList) {
      long id = entity.getKey().getId();
      System.out.println("id: " + id);
    
      String name = (String) entity.getProperty(NAME);
      String category = (String) entity.getProperty(CATEGORY);
      String content = (String) entity.getProperty(CONTENT);
      String comment = (String) entity.getProperty(COMMENT);
 
      Recommendation recommendation = new Recommendation(id, name, category, content, comment);

      myToDos.add(recommendation);
    }

    String jsonResponse = convertToJson(myToDos);
    response.setContentType("application/json");
    response.getWriter().println(jsonResponse);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {   
    String name = request.getParameter(NAME);
    String category = request.getParameter(CATEGORY);
    String content = request.getParameter(CONTENT);
    String comment = request.getParameter(COMMENT);

    Entity recommendation = new Entity("Recommendation");
    recommendation.setProperty(NAME, name);
    recommendation.setProperty(CATEGORY, category);
    recommendation.setProperty(CONTENT, content);
    recommendation.setProperty(COMMENT, comment);
    recommendation.setProperty("timestamp", System.currentTimeMillis());

    datastore.put(recommendation);
  }

  private static String convertToJson(List<Recommendation> messages) { 
    Gson gson = new Gson();
    String jsonString = gson.toJson(messages);
    return jsonString;
  }
}
