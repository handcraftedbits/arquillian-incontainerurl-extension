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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DeploymentVerificationBean {
     public static final String FILE_DEPLOYMENT_NAME = "deployment.name";

     public String getDeploymentName () {
          final InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/" +
               DeploymentVerificationBean.FILE_DEPLOYMENT_NAME);

          try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
               final String line = reader.readLine();

               if (line == null) {
                    return "";
               }

               return line;
          }

          catch (final IOException e) {
               return "";
          }
     }
}
