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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * Client program to delete a quote via the HTTP API.
 * <p>
 * For some background on the use of RestClient in the implementation, compared to alternatives,
 * see <a href="https://digma.ai/restclient-vs-webclient-vs-resttemplate/">restclient-vs-webclient-vs-resttemplate</a>.
 *
 * @author Chris de Vreeze
 */
public class QuoteDeleter {

    private final Logger logger = LoggerFactory.getLogger(QuoteDeleter.class);

    private final RestClient restClient;

    public QuoteDeleter(RestClient restClient) {
        this.restClient = restClient;
    }

    public void deleteQuote(long quoteId) {
        logger.info(String.format("Trying to delete quote with ID %d", quoteId));
        var statusCode = restClient.delete().uri(String.format("/quotes/%d", quoteId))
                .retrieve()
                .toBodilessEntity()
                .getStatusCode();
        logger.info(String.format("Response status code: %s", statusCode));
    }

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);
        var quoteId = Long.parseLong(args[0]);

        var appContext = new AnnotationConfigApplicationContext(ClientConfig.class);

        var restClient = appContext.getBean("restClient", RestClient.class);
        var quoteDeleter = new QuoteDeleter(restClient);

        quoteDeleter.deleteQuote(quoteId);
    }
}
