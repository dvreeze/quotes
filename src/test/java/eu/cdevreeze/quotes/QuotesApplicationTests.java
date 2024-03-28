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

package eu.cdevreeze.quotes;

import eu.cdevreeze.quotes.repository.QuoteRepository;
import eu.cdevreeze.quotes.service.QuoteService;
import eu.cdevreeze.quotes.web.QuotesAdminController;
import eu.cdevreeze.quotes.web.QuotesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Quotes application test.
 *
 * @author Chris de Vreeze
 */
@SpringBootTest
class QuotesApplicationTests {

    private final QuotesController quotesController;
    private final QuotesAdminController quotesAdminController;
    private final QuoteService quoteService;
    private final QuoteRepository quoteRepository;

    @Autowired
    public QuotesApplicationTests(
            QuotesController quotesController,
            QuotesAdminController quotesAdminController,
            QuoteService quoteService,
            QuoteRepository quoteRepository) {
        this.quotesController = quotesController;
        this.quotesAdminController = quotesAdminController;
        this.quoteService = quoteService;
        this.quoteRepository = quoteRepository;
    }

    @Test
    void contextLoads() {
        assertNotNull(quotesController);
        assertNotNull(quotesAdminController);
        assertNotNull(quoteService);
        assertNotNull(quoteRepository);
    }

}
