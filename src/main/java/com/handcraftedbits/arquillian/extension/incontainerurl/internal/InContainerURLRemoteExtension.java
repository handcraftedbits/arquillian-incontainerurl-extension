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

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.test.spi.TestEnricher;

final class InContainerURLRemoteExtension implements RemoteLoadableExtension {
     @Override
     public void register (final ExtensionBuilder builder) {
          builder.service(TestEnricher.class, InContainerURLTestEnricher.class);
     }
}
