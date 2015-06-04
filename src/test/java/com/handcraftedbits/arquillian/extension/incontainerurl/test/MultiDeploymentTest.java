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
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public final class MultiDeploymentTest {
     public static final String DEPLOYMENT_NAME_A = "deploymentA";
     public static final String DEPLOYMENT_NAME_B = "deploymentB";
     public static final String DEPLOYMENT_NAME_DEFAULT = "_DEFAULT_";

     @SuppressWarnings("unused")
     @InContainerResource
     private URL url;

     @Inject
     private DeploymentVerificationBean verificationBean;

     @Deployment(name = MultiDeploymentTest.DEPLOYMENT_NAME_A)
     public static WebArchive createDeploymentA () {
          return DeploymentTestUtil.createDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_A);
     }

     @Deployment(name = MultiDeploymentTest.DEPLOYMENT_NAME_B)
     public static WebArchive createDeploymentB () {
          return DeploymentTestUtil.createDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_B);
     }

     @Deployment
     public static WebArchive createDeploymentDefault () {
          return DeploymentTestUtil.createDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_DEFAULT);
     }

     @Test
     @OperateOnDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_A)
     public void testDeploymentURLA () throws Exception {
          DeploymentTestUtil.verifyDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_A, this.verificationBean, this.url);
     }

     @Test
     @OperateOnDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_A)
     public void testDeploymentURLAAsParameter (@InContainerResource final URL url) throws Exception {
          DeploymentTestUtil.verifyDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_A, this.verificationBean, url);
     }

     @Test
     @OperateOnDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_B)
     public void testDeploymentURLB () throws Exception {
          DeploymentTestUtil.verifyDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_B, this.verificationBean, this.url);
     }

     @Test
     @OperateOnDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_B)
     public void testDeploymentURLBAsParameter (@InContainerResource final URL url) throws Exception {
          DeploymentTestUtil.verifyDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_B, this.verificationBean, url);
     }

     @Test
     public void testDeploymentURLDefault () throws Exception {
          DeploymentTestUtil.verifyDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_DEFAULT, this.verificationBean,
               this.url);
     }

     @Test
     public void testDeploymentURLDefaultAsParameter (@InContainerResource final URL url) throws Exception {
          DeploymentTestUtil.verifyDeployment(MultiDeploymentTest.DEPLOYMENT_NAME_DEFAULT, this.verificationBean, url);
     }

     @Test
     @RunAsClient
     public void testDeploymentURLMissingForClient () {
          Assert.assertNull(this.url);
     }
}
