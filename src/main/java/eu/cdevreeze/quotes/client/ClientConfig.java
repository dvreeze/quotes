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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Non-idiomatic Spring Configuration for system properties "scheme", "hostName" and "port".
 * Make sure this configuration will never be scanned by the server-side Spring wiring.
 *
 * @author Chris de Vreeze
 */
@Configuration
public class ClientConfig {

    private final String prefix = "restclient" + ".";

    @Bean
    public String scheme() {
        return System.getProperty(prefix + "scheme", "http");
    }

    @Bean
    public String hostName() {
        return System.getProperty(prefix + "hostName", "localhost");
    }

    @Bean
    public int port() {
        return Integer.parseInt(System.getProperty(prefix + "port", "8081"));
    }

    @Bean
    public RestClient restClient() {
        var baseUrl = new DefaultUriBuilderFactory().builder()
                .scheme(scheme()).host(hostName()).port(port()).toUriString();

        return RestClient.create(baseUrl);
    }
}
