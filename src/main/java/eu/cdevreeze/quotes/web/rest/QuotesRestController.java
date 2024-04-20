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

package eu.cdevreeze.quotes.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * Quotes web REST controller.
 * <p>
 * Note that the RestController annotation is itself annotated with the Controller and ResponseBody
 * annotations. Hence, it is (indirectly) meta-annotated with the Component annotation (so it is a
 * candidate for component scanning). The ResponseBody annotation binds controller method return values
 * directly to the HTTP response payload, instead of a model and view.
 *
 * @author Chris de Vreeze
 */
@RestController
public class QuotesRestController {

    private final QuoteService quoteService;

    public QuotesRestController(QuoteService quoteService) {
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

    @GetMapping(value = "/quotesBySubject.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ImmutableList<Quote> quotesBySubject(@RequestParam String subject) {
        return quoteService.findBySubject(subject);
    }

    @GetMapping(value = "/quotesByAttributedTo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ImmutableList<Quote> quotesByAttributedTo(@RequestParam String attributedTo) {
        return quoteService.findByAttributedTo(attributedTo);
    }

    // Below I considered using the PUT HTTP method, but that would require idempotency. Hence, the use of HTTP POST.

    @PostMapping(value = "/quote", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Quote> addQuote(RequestEntity<QuoteData> requestEntity) throws JsonProcessingException {
        // We need the RequestEntity "wrapper" or RequestBody annotation, or else the request body is null
        // Thus we tell Spring MVC explicitly not to treat the QuoteData-typed method parameter as request parameter
        var quoteData = requestEntity.getBody();
        var quote = quoteService.addQuote(quoteData);
        return new ResponseEntity<>(quote, HttpStatus.OK);
    }

    @DeleteMapping(value = "/quotes/{quoteId}")
    public void deleteQuote(@PathVariable long quoteId) {
        quoteService.deleteQuote(quoteId);
    }
}
