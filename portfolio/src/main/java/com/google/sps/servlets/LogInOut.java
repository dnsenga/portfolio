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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/log-in-out")
public class LogInOut extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      response.getWriter().println("<p>Hello " + userEmail + "!</p>");

      response.getWriter().println("<form action=\"/delete-data\" method=\"POST\">");
      response.getWriter().println("<input type=\"submit\" name=\"delete-comments\" value=\"Delete Your Most Recent Comment\" /> </form>");
      response.getWriter().println("<div class=\"send-comment-container\"><h3>Leave <span class=\"text-primary\"> A Comment</span></h3><form action=\"/data\" method=\"POST\">");
      response.getWriter().println("<textarea placeholder=\"Add Your Name\" id=\"name-input\" name=\"name-input\"></textarea>");
      response.getWriter().println("<textarea placeholder=\"Add Your Comment\" id=\"comment-input\" name=\"comment-input\" pattern=\"[A-Za-z0-9]{1,20}\"></textarea> ");
      response.getWriter().println("<div class=\"comment-btn\"> <input type=\"submit\" value=\"Comment\"/> ");
      response.getWriter().println("<button id=\"clear\" href=\"#\">Cancel</button>");
      response.getWriter().println("</div></form></div>");

      response.getWriter().println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");

      
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      response.getWriter().println("<p>Hello stranger. Please login to add or delete comments. </p>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }
}