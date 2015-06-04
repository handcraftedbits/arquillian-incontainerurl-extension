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
package com.handcraftedbits.arquillian.extension.incontainerurl.test;

import java.net.URL;

import javax.inject.Inject;

import com.handcraftedbits.arquillian.extension.incontainerurl.api.InContainerResource;
import com.handcraftedbits.arquillian.extension.incontainerurl.test.util.DeploymentTestUtil;
import com.handcraftedbits.arquillian.extension.incontainerurl.test.verification.DeploymentVerificationBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public final class EARDeploymentTest {
     private static final String WAR_NAME = "webapp";

     @SuppressWarnings("unused")
     @InContainerResource
     private URL url;

     @Inject
     private DeploymentVerificationBean verificationBean;

     @Deployment
     public static EnterpriseArchive createDeployment () {
          return ShrinkWrap.create(EnterpriseArchive.class)
               .addAsModules(DeploymentTestUtil.createDeployment(EARDeploymentTest.WAR_NAME));
     }

     @Test
     public void testDeploymentURL () throws Exception {
          DeploymentTestUtil.verifyDeployment(EARDeploymentTest.WAR_NAME, this.verificationBean, this.url);
     }

     @Test
     public void testDeploymentURLAsParameter (@InContainerResource final URL url) throws Exception {
          DeploymentTestUtil.verifyDeployment(EARDeploymentTest.WAR_NAME, this.verificationBean, url);
     }

     @Test
     @RunAsClient
     public void testDeploymentURLMissingForClient () {
          Assert.assertNull(this.url);
     }
}
