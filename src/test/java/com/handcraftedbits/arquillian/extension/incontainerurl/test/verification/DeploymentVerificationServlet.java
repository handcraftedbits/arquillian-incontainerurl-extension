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
package com.handcraftedbits.arquillian.extension.incontainerurl.test.verification;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/" + DeploymentVerificationServlet.PATH)
public final class DeploymentVerificationServlet extends HttpServlet {
     public static final String PATH = "deploymentVerification";

     @Inject
     private DeploymentVerificationBean verificationBean;

     @Override
     protected void doGet (final HttpServletRequest request, final HttpServletResponse response)
          throws ServletException, IOException {
          final PrintWriter writer;

          response.setContentType("text/plain");

          writer = response.getWriter();

          writer.write(this.verificationBean.getDeploymentName());
          writer.flush();
     }

}
