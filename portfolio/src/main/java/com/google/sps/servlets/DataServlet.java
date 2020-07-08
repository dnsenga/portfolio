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
import java.util.Collections;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  int numberOfCommentsToDisplay = 5;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("commentEntity").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String commentText = (String) entity.getProperty("comment");
      String sentimentScore = String.valueOf(entity.getProperty("sentimentScore"));
      sentimentScore = sentimentScore.substring(0,Math.min(4,sentimentScore.length()));
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, name, commentText, sentimentScore, timestamp);
      comments.add(comment);
    }
    // Shuffle the comments
    Collections.shuffle(comments);

    response.setContentType("application/json");
    // Convert the server stats to JSON
    Gson gson = new Gson();
    String json = gson.toJson(comments.subList(0, Math.min(numberOfCommentsToDisplay, comments.size())));

    // Send the JSON as the response
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    numberOfCommentsToDisplay = getUserChoice(request);
    String name = request.getParameter("name-input");
    String newComment = request.getParameter("comment-input");
    long timestamp = System.currentTimeMillis();

    if (newComment != null){
      // calculate sentiment
      Document doc = Document.newBuilder().setContent(newComment).setType(Document.Type.PLAIN_TEXT).build();
      LanguageServiceClient languageService = LanguageServiceClient.create();
      Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
      float sentimentScoreFloat = sentiment.getScore();
      languageService.close();
      String sentimentScore =String.valueOf(sentimentScoreFloat);

      // add comment
      Entity commentEntity = new Entity("commentEntity");
      commentEntity.setProperty("name", name);
      commentEntity.setProperty("comment", newComment);
      commentEntity.setProperty("sentimentScore", sentimentScore);
      commentEntity.setProperty("timestamp", timestamp);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }
    // Redirect back to the HTML page.
    response.sendRedirect("index.html");
  }

  /** Returns the choice entered by the user, or 5 if the choice was invalid. */
  private int getUserChoice(HttpServletRequest request) {
    // Get the input from the form.
    String userChoiceString = request.getParameter("number-of-comments");

    // Convert the input to an int.
    int userChoice;
    try {
      userChoice = Integer.parseInt(userChoiceString);
    } catch (NumberFormatException e) {
      return 5;
    }

    // Check that the input is between 1 and 20.
    if (userChoice < 1 || userChoice > 20) {
      return 5;
    }

    return userChoice;
  }
}