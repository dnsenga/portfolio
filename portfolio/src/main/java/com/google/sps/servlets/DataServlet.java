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

import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.print("INSIDE THE DOGET FUNCTION");
    Query query = new Query("entityComment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String commentText = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");
      System.out.println(id + "\t" + name + "\t" +  commentText + "\t" + timestamp);

      Comment comment = new Comment(id, name, commentText, timestamp);
      comments.add(comment);
    }

    response.setContentType("application/json");
    // Convert the server stats to JSON
    String json = convertToJson(comments);
    // Send the JSON as the response
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.print("INSIDE THE DOPOST FUNCTION");
    String name = request.getParameter("name-input");
    String newComment = request.getParameter("comment-input");
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("entityComment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("comment", newComment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /**
   * Converts a ServerStats instance into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
  private String convertToJson(List<Comment> comments) {
      System.out.println(comments);
    String json = "[";
    for (int i = 0; i < comments.size(); i++){
        json += "{\"time\": \"" + comments.get(i).timestamp + "\", ";
        json += "\"name\": \"" + comments.get(i).name + "\", ";
        json += "\"comment\": \"" + comments.get(i).comment + "\"}, ";
    }
    json = json.substring(0, json.length() - 2);
    json += "]";
    System.out.println(json);
    return json;
  }
}
