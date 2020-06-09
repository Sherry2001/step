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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
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

  /** This section copied from Walkthrough tutorial **/
  /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's dev server, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
