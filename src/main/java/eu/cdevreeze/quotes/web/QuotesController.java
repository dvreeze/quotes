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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import eu.cdevreeze.quotes.service.QuoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
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

    // The prettified JSON strings returned below increase the payload size, but that's accepted here

    @GetMapping(value = "/randomQuote", produces = "application/json")
    public String randomQuote() {
        var allQuotes = quoteService.findAllQuotes();
        var random = new Random();
        var randomIdx = random.nextInt(allQuotes.size());
        var randomQuote = allQuotes.get(randomIdx);

        var sw = new StringWriter();
        try {
            getObjectMapper().writeValue(sw, randomQuote);
            sw.write("\n");
            sw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return replaceWindowsNewline(sw.toString());
    }

    @GetMapping(value = "/quotes", produces = "application/json")
    public String quotes() {
        var allQuotes = quoteService.findAllQuotes();

        var sw = new StringWriter();
        try {
            getObjectMapper().writeValue(sw, allQuotes);
            sw.write("\n");
            sw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return replaceWindowsNewline(sw.toString());
    }

    private ObjectMapper getObjectMapper() {
        return JsonMapper.builder()
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    private String replaceWindowsNewline(String s) {
        return s.replace("\r\n", "\n");
    }
}
