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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.arquillian.container.spi.event.container.AfterDeploy;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public final class AfterDeployObserver {
     private static final Logger logger = Logger.getLogger(AfterDeployObserver.class.getName());

     @SuppressWarnings("unused")
     @Inject
     private Instance<ProtocolMetaData> protocolMetaData;

     private boolean areServletsAllInSameContext (final List<Servlet> servlets) {
          final Set<String> contexts = new HashSet<>();

          for (final Servlet servlet : servlets) {
               contexts.add(servlet.getContextRoot());
          }

          return contexts.size() == 1;
     }

     @SuppressWarnings("unused")
     public void onAfterDeploy (@Observes final AfterDeploy afterDeployEvent) {
          final URL baseURL;

          logger.log(Level.FINE, String.format("begin after deployment processing for deployment '%s'",
               afterDeployEvent.getDeployment().getName()));

          baseURL = getBaseURL();

          if (baseURL == null) {
               return;
          }

          try {
               final Map<String, String> parameters = new HashMap<>();
               final String result;
               final URL servletURL = new URL(baseURL, DeploymentURLManagerServlet.PATH);

               parameters.put(DeploymentURLManagerServlet.PARAM_URL, baseURL.toExternalForm());

               result = HTTPUtil.doPost(servletURL, parameters);

               if (!result.equals(DeploymentURLManagerServlet.STATUS_OK)) {
                    logger.log(Level.SEVERE, "deployment URL manager servlet did not succeed; injection will not " +
                         "occur");
               }
          }

          catch (final Throwable e) {
               logger.log(Level.SEVERE, "unable to communicate with deployment URL manager servlet", e);
          }
     }

     private URL getBaseURL () {
          final HTTPContext httpContext;
          final Collection<HTTPContext> httpContexts = this.protocolMetaData.get().getContexts(HTTPContext.class);

          if ((httpContexts == null) || httpContexts.isEmpty()) {
               logger.log(Level.SEVERE, "unable to find HTTP contexts in protocol metadata; injection will not occur");

               return null;
          }

          httpContext = httpContexts.iterator().next();

          if (areServletsAllInSameContext(httpContext.getServlets())) {
               try {
                    return httpContext.getServlets().get(0).getBaseURI().toURL();
               }

               catch (final MalformedURLException e) {
                    logger.log(Level.SEVERE, String.format("unable to parse URL '%s'",
                         httpContext.getServlets().get(0).getBaseURI().toASCIIString()), e);

                    return null;
               }
          }

          logger.log(Level.SEVERE, String.format("servlets associated with HTTP context '%s' are not all registered " +
               "to the same context root", httpContext.getName()));

          return null;
     }
}
