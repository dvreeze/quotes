/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.quotes.web;

import eu.cdevreeze.quotes.internal.utils.ObjectMappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Additional Spring beans needed in the web layer.
 *
 * @author Chris de Vreeze
 */
@Configuration
public class WebSupportConfig {

    /**
     * Replacement for the default MappingJackson2HttpMessageConverter.
     * This replacement is aware of Guava immutable collections, and it prettifies JSON output.
     * <p>
     * This message converter is (for example) used in RestController-annotated controllers to create
     * an HTTP response body from Java records (e.g. containing Guava collections). This Configuration-annotated
     * bean is found by Component scanning, and this specific Bean-annotated bean is wired automatically into a
     * RequestMappingHandlerAdapter, which is used by the DispatcherServlet (as a HandlerAdapter) to
     * handle RequestMapping-(meta-)annotated (controller) methods.
     */
    @Bean
    public HttpMessageConverter<Object> httpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(ObjectMappers.getObjectMapper());
    }
}
