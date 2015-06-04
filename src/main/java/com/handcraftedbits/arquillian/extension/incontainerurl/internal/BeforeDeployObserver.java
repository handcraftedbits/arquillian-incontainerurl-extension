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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.handcraftedbits.arquillian.extension.incontainerurl.api.InContainerResource;
import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class BeforeDeployObserver {
     private static final Logger logger = Logger.getLogger(BeforeDeployObserver.class.getName());

     private JavaArchive createArchive () {
          return ShrinkWrap.create(JavaArchive.class, "arquillian-incontainerurl-extension.jar")
               .addClasses(InContainerResource.class,
                    DeploymentURLManager.class,
                    DeploymentURLManagerServlet.class,
                    InContainerURLRemoteExtension.class,
                    InContainerURLTestEnricher.class)
               .addAsServiceProvider(RemoteLoadableExtension.class, InContainerURLRemoteExtension.class);
     }

     @SuppressWarnings("unused")
     public void onBeforeDeploy (@Observes final BeforeDeploy beforeDeployEvent) {
          final Archive<?> archive = beforeDeployEvent.getDeployment().getArchive();

          logger.log(Level.FINE, String.format("begin before deployment processing for deployment '%s'",
               beforeDeployEvent.getDeployment().getName()));

          // If the test is deploying an EAR we have to fix things up by finding the WAR (Arquillian won't let you have
          // more than one so we'll just take the first one we find) and adding our JAR to it.

          if (archive instanceof EnterpriseArchive) {
               WebArchive webArchive = null;

               for (final Node node : archive.getContent().values()) {
                    if (node.getAsset() instanceof ArchiveAsset) {
                         final Archive<?> curArchive = ((ArchiveAsset) node.getAsset()).getArchive();

                         if (curArchive instanceof WebArchive) {
                              webArchive = curArchive.as(WebArchive.class);

                              break;
                         }
                    }
               }

               if (webArchive != null) {
                    logger.log(Level.FINE, String.format("adding @InContainerResource extension to web archive %s in " +
                         "enterprise archive %s", webArchive.getName(), archive.getName()));

                    webArchive.addAsLibrary(createArchive());
               }
          }

          else {
               // For a WAR, just add our JAR.  Other archive types we'll ignore.

               if (archive instanceof WebArchive) {
                    logger.log(Level.FINE, String.format("adding @InContainerResource extension to web archive %s",
                         archive.getName()));

                    archive.as(WebArchive.class).addAsLibrary(createArchive());
               }
          }
     }
}
