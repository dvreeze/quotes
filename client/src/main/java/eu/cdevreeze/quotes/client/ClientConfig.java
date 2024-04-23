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

package eu.cdevreeze.quotes.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

/**
 * Non-idiomatic Spring Configuration for system properties "scheme", "hostName" and "port",
 * and beans for HttpMessageConverter, Jackson ObjectMapper and RestClient.
 *
 * @author Chris de Vreeze
 */
@Configuration
public class ClientConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Bean
    public HttpMessageConverter<?> httpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    @Bean
    public RestClient restClient(
            @Value("${restclient.scheme}") String scheme,
            @Value("${restclient.host}") String host,
            @Value("${restclient.port}") int port
    ) {
        var baseUrl = new DefaultUriBuilderFactory().builder()
                .scheme(scheme).host(host).port(port).toUriString();

        var restTemplate = new RestTemplate(List.of(httpMessageConverter()));

        return RestClient.builder(restTemplate).baseUrl(baseUrl).build();
    }
}
