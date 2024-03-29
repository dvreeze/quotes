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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.service.QuoteService;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Quotes web controller.
 *
 * @author Chris de Vreeze
 */
@RestController
public class QuotesController {

    private final QuoteService quoteService;

    public QuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping(value = "/randomQuote.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Quote randomQuote() {
        var allQuotes = quoteService.findAllQuotes();
        var random = new Random();
        var randomIdx = random.nextInt(allQuotes.size());
        return allQuotes.get(randomIdx);
    }

    @GetMapping(value = "/quotes.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ImmutableList<Quote> quotes() {
        return quoteService.findAllQuotes();
    }

    @PostMapping(value = "/addQuote", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Quote addQuote(RequestEntity<QuoteData> requestEntity) throws JsonProcessingException {
        // We need the RequestEntity "wrapper", or else the body is null
        var quoteData = requestEntity.getBody();
        return quoteService.addQuote(quoteData);
    }
}
