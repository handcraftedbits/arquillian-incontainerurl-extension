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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.handcraftedbits.arquillian.extension.incontainerurl.api.InContainerResource;
import org.jboss.arquillian.test.spi.TestEnricher;

public final class InContainerURLTestEnricher implements TestEnricher {
     private static final Logger logger = Logger.getLogger(InContainerURLTestEnricher.class.getName());

     @Override
     public void enrich (final Object testCase) {
          for (final Field field : getAllFields(testCase.getClass())) {
               injectField(testCase, field);
          }
     }

     private List<Field> getAllFields (final Class<?> cls) {
          final List<Field> fields = new LinkedList<>();

          getAllFields(cls, fields);

          return fields;
     }

     private void getAllFields (final Class<?> cls, final List<Field> fields) {
          fields.addAll(Arrays.asList(cls.getDeclaredFields()));

          if (cls.getSuperclass() != null) {
               getAllFields(cls.getSuperclass(), fields);
          }
     }

     private URL getBaseURL () {
          return DeploymentURLManager.getURLForDeployment();
     }

     private void injectField (final Object instance, final Field field) {
          // Field must be annotated with @InContainerResource and be of type java.net.URL.

          if ((field.getAnnotation(InContainerResource.class) != null) && field.getType().equals(URL.class)) {
               try {
                    field.setAccessible(true);

                    field.set(instance, getBaseURL());
               }

               catch (final Throwable e) {
                    logger.log(Level.SEVERE, String.format("unable to inject URL into field %s.%s",
                         field.getDeclaringClass().getName(), field.getName()), e);
               }
          }
     }

     @Override
     public Object[] resolve (final Method method) {
          int i = 0;
          final Object result[] = new Object[method.getParameterTypes().length];

          // Inject into any java.net.URL parameters that are annotated with @InContainerResource.

          for (final Class<?> param : method.getParameterTypes()) {
               if (param.equals(URL.class)) {
                    final Annotation annotations[] = method.getParameterAnnotations()[i];

                    for (final Annotation annotation : annotations) {
                         if (annotation.annotationType().equals(InContainerResource.class)) {
                              result[i] = getBaseURL();
                         }
                    }
               }

               ++i;
          }

          return result;
     }
}
