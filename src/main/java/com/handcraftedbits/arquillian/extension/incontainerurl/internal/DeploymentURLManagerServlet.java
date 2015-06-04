/*
 * #%L
 * Arquillian In-Container URL Extension
 * %%
 * Copyright (C) 2015 HandcraftedBits
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.handcraftedbits.arquillian.extension.incontainerurl.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/" + DeploymentURLManagerServlet.PATH)
public final class DeploymentURLManagerServlet extends HttpServlet {
     private static final Logger logger = Logger.getLogger(DeploymentURLManagerServlet.class.getName());

     static final String PARAM_URL = "url";
     static final String PATH = "_deploymentURLManager";
     private static final String STATUS_FAILED = "FAILED";
     static final String STATUS_OK = "OK";

     @Override
     protected void doPost (final HttpServletRequest request, final HttpServletResponse response)
          throws ServletException, IOException {
          final String messageFailed = "POST request failed";
          final URL url;
          final String urlStr;
          final PrintWriter writer;

          logger.log(Level.FINE, "handling POST request");

          response.setContentType("text/plain");

          writer = response.getWriter();

          // Check parameters.

          urlStr = request.getParameter(DeploymentURLManagerServlet.PARAM_URL);

          if (urlStr == null) {
               logger.log(Level.SEVERE, "no URL parameter specified");

               writer.println(DeploymentURLManagerServlet.STATUS_FAILED);
               writer.flush();

               logger.log(Level.FINE, messageFailed);

               return;
          }

          try {
               url = new URL(urlStr);
          }

          catch (final MalformedURLException e) {
               logger.log(Level.SEVERE, String.format("invalid URL parameter '%s' specified", urlStr));
               logger.log(Level.FINE, messageFailed);

               return;
          }

          // Update the URL in the deployment manager and finish.

          DeploymentURLManager.setURLForDeployment(url);

          writer.println(DeploymentURLManagerServlet.STATUS_OK);
          writer.flush();

          logger.log(Level.FINE, "POST request succeeded");
     }
}
