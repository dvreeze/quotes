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
import eu.cdevreeze.quotes.internal.utils.ObjectMappers;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Client program to add a quote via the HTTP API. The program input is a file path where the file
 * contains a QuoteData instance as JSON string.
 * <p>
 * For some background on the use of RestClient in the implementation, compared to alternatives,
 * see <a href="https://digma.ai/restclient-vs-webclient-vs-resttemplate/">restclient-vs-webclient-vs-resttemplate</a>.
 *
 * @author Chris de Vreeze
 */
public class QuoteAdder {

    private final Logger logger = LoggerFactory.getLogger(QuoteAdder.class);

    private final RestClient restClient;

    public QuoteAdder(RestClient restClient) {
        this.restClient = restClient;
    }

    public Quote addQuote(QuoteData quoteData) throws JsonProcessingException {
        logger.info(String.format("Trying to add a quote attributed to %s", quoteData.attributedTo()));

        ResponseEntity<Quote> responseEntity = restClient.post()
                .uri("/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(quoteData)
                .retrieve()
                .toEntity(Quote.class);

        logger.info(String.format("Response status code: %s", responseEntity.getStatusCode()));
        logger.info(String.format(
                "Response payload:%n%s",
                ObjectMappers.getObjectMapper().writer().writeValueAsString(responseEntity.getBody()))
        );

        return responseEntity.getBody();
    }

    public static void main(String[] args) throws IOException {
        Objects.checkIndex(0, args.length);
        var jsonInputFile = Path.of(args[0]);
        var quoteDataAsJsonString = Files.readString(jsonInputFile);

        var objectMapper = ObjectMappers.getObjectMapper();
        QuoteData quoteData = objectMapper.readValue(quoteDataAsJsonString, QuoteData.class);

        var appContext = new AnnotationConfigApplicationContext(ClientConfig.class);

        var restClient = appContext.getBean("restClient", RestClient.class);
        var quoteAdder = new QuoteAdder(restClient);

        quoteAdder.addQuote(quoteData);
    }
}
