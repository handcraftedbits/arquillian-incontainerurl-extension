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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HTTPUtil {
     private static final Logger logger = Logger.getLogger(HTTPUtil.class.getName());

     private static final String ENCODING_DEFAULT = "UTF-8";
     private static final int TIMEOUT_DEFAULT = 10 * 1000;

     private HTTPUtil () {
     }

     public static String doGet (final URL url) throws IOException {
          final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          final StringBuilder stringBuilder = new StringBuilder();

          // Set up the connection.

          connection.setConnectTimeout(HTTPUtil.TIMEOUT_DEFAULT);
          connection.setReadTimeout(HTTPUtil.TIMEOUT_DEFAULT);
          connection.setDoOutput(true);
          connection.setRequestMethod("GET");

          logger.log(Level.FINE, String.format("Invoking GET on %s", url.toExternalForm()));

          connection.connect();

          // Read the response.

          try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
               String line;

               while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
               }
          }

          return stringBuilder.toString().trim();
     }

     static String doPost (final URL url, final Map<String, String> parameters) throws IOException {
          final byte bodyBytes[];
          final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          int i = 0;
          final int length = parameters.size();
          StringBuilder stringBuilder = new StringBuilder();

          // Set up the connection.

          connection.setConnectTimeout(HTTPUtil.TIMEOUT_DEFAULT);
          connection.setReadTimeout(HTTPUtil.TIMEOUT_DEFAULT);
          connection.setDoInput(true);
          connection.setDoOutput(true);

          // Encode the parameters.

          for (final String key : parameters.keySet()) {
               stringBuilder.append(key);
               stringBuilder.append('=');
               stringBuilder.append(URLEncoder.encode(parameters.get(key), HTTPUtil.ENCODING_DEFAULT));

               if (i++ < (length - 1)) {
                    stringBuilder.append('&');
               }
          }

          // Finish setting up the request and write the body.

          bodyBytes = stringBuilder.toString().getBytes(HTTPUtil.ENCODING_DEFAULT);

          connection.setRequestMethod("POST");
          connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
          connection.setRequestProperty("Content-Length", "" + bodyBytes.length);

          try (OutputStream output = connection.getOutputStream()) {
               output.write(bodyBytes);
               output.flush();
          }

          if (logger.isLoggable(Level.FINE)) {
               stringBuilder = new StringBuilder("\n\tPOSTing to ");

               stringBuilder.append(url.toExternalForm());
               stringBuilder.append(" with parameters:\n");

               i = 0;

               for (final String key : parameters.keySet()) {
                    stringBuilder.append('\t');
                    stringBuilder.append(key);
                    stringBuilder.append('=');
                    stringBuilder.append(parameters.get(key));

                    if (i++ < (length - 1)) {
                         stringBuilder.append('\n');
                    }
               }

               logger.log(Level.FINE, stringBuilder.toString());
          }

          connection.connect();

          // Read the response.

          stringBuilder = new StringBuilder();

          try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
               String line;

               while ((line = reader.readLine()) != null)
               {
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
               }
          }

          return stringBuilder.toString().trim();
     }
}
