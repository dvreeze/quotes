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

import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Quotes web controller.
 *
 * @author Chris de Vreeze
 */
@RestController
public class QuotesController {

    private final QuoteService quoteService;

    public QuotesController(@Autowired QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping(value = "/randomQuote", produces = "application/json")
    public Map<String, Object> randomQuote() {
        var allQuotes = quoteService.findAllQuotes();
        var random = new Random();
        var randomIdx = random.nextInt(allQuotes.size());
        var randomQuote = allQuotes.get(randomIdx);

        return convertQuote(randomQuote);
    }

    @GetMapping(value = "/quotes", produces = "application/json")
    public List<Map<String, Object>> quotes() {
        var allQuotes = quoteService.findAllQuotes();
        return allQuotes.stream().map(this::convertQuote).toList();
    }

    private Map<String, Object> convertQuote(Quote quote) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", quote.idOption().orElse(0));
        result.put("text", quote.text());
        result.put("attributedTo", quote.attributedTo());
        result.put("subjects", quote.subjects());
        return result;
    }
}
