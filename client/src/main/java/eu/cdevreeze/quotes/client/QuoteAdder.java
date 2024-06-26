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
import com.google.common.base.Preconditions;
import eu.cdevreeze.quotes.client.model.Quote;
import eu.cdevreeze.quotes.client.model.QuoteData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Client program to add quotes via the HTTP API. The program input is a file path where the file
 * contains a collection of QuoteData instances as JSON string.
 * <p>
 * For some background on the use of RestClient in the implementation, compared to alternatives,
 * see <a href="https://digma.ai/restclient-vs-webclient-vs-resttemplate/">restclient-vs-webclient-vs-resttemplate</a>.
 *
 * @author Chris de Vreeze
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@Import(ClientConfig.class)
public class QuoteAdder implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(QuoteAdder.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public QuoteAdder(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public List<Quote> addQuotes(List<QuoteData> quoteDataRecords) {
        return quoteDataRecords.stream().map(this::addQuote).toList();
    }

    public Quote addQuote(QuoteData quoteData) {
        logger.info(String.format("Trying to add a quote attributed to %s", quoteData.attributedTo()));

        ResponseEntity<Quote> responseEntity = restClient.post()
                .uri("/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(quoteData)
                .retrieve()
                .toEntity(Quote.class);

        logger.info(String.format("Response status code: %s", responseEntity.getStatusCode()));
        try {
            logger.info(String.format(
                    "Response payload:%n%s",
                    objectMapper.writer().writeValueAsString(responseEntity.getBody()))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return responseEntity.getBody();
    }

    @Override
    public void run(String... args) throws IOException {
        Objects.checkIndex(0, args.length);
        var jsonInputFile = Path.of(args[0]);
        var quoteDataAsJsonString = Files.readString(jsonInputFile);

        List<QuoteData> quoteDataRecords =
                Arrays.stream(objectMapper.readValue(quoteDataAsJsonString, QuoteData[].class)).toList();

        var quotes = addQuotes(quoteDataRecords);
        Preconditions.checkArgument(quotes.size() == quoteDataRecords.size());
    }

    public static void main(String[] args) {
        SpringApplication.run(QuoteAdder.class, args);
    }
}
