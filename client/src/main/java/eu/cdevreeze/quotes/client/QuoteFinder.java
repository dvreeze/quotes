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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.client.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Client program to find all quotes via the HTTP API.
 * <p>
 * Run with:
 * <pre>
 * cd client
 * ../mvnw spring-boot:run -Pfind
 * cd ..
 * </pre>
 * <p>
 * For some background on the use of RestClient in the implementation, compared to alternatives,
 * see <a href="https://digma.ai/restclient-vs-webclient-vs-resttemplate/">restclient-vs-webclient-vs-resttemplate</a>.
 *
 * @author Chris de Vreeze
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@Import(ClientConfig.class)
public class QuoteFinder implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(QuoteAdder.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public QuoteFinder(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public ImmutableList<Quote> findAllQuotes() {
        logger.info("Trying to retrieve all quotes");

        ResponseEntity<ImmutableList<Quote>> responseEntity = restClient.get()
                .uri("/quotes.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        logger.info(String.format("Response status code: %s", responseEntity.getStatusCode()));

        return responseEntity.getBody();
    }

    @Override
    public void run(String... args) throws JsonProcessingException {
        var quotes = findAllQuotes();

        System.out.printf("%s%n", objectMapper.writer().writeValueAsString(quotes));
    }

    public static void main(String[] args) {
        SpringApplication.run(QuoteFinder.class, args);
    }
}
