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

import eu.cdevreeze.quotes.model.SampleData;
import eu.cdevreeze.quotes.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Quotes admin web REST controller.
 *
 * @author Chris de Vreeze
 */
@RestController
@RequestMapping("/admin")
public class QuotesAdminRestController {

    private final Logger logger = LoggerFactory.getLogger(QuotesAdminRestController.class);

    private final QuoteService quoteService;

    public QuotesAdminRestController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/loadSampleQuotes")
    public void loadSampleQuotes() {
        if (quoteService.findAllQuotes().isEmpty()) {
            logger.info(String.format("Loading %d sample quotes into the database", SampleData.allQuotes.size()));
            SampleData.allQuotes.forEach(quoteService::addQuote);
        } else {
            logger.warn("Not loading any sample quotes into the database, because it is already non-empty");
        }
    }
}
