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

package eu.cdevreeze.quotes.appconfig;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.model.SampleData;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import eu.cdevreeze.quotes.repository.nonpersistent.NonPersistentQuoteRepository;
import eu.cdevreeze.quotes.service.QuoteService;
import eu.cdevreeze.quotes.service.impl.TransactionalQuoteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test Spring Configuration for service and repository layer.
 *
 * @author Chris de Vreeze
 */
@Configuration
@ConditionalOnProperty(value = "useInMemoryRepositories", havingValue = "true")
public class TestServiceAppConfig implements ServiceAppConfigApi {

    @Bean
    public QuoteRepository quoteRepository() {
        return new NonPersistentQuoteRepository(getAllQuotes());
    }

    @Bean
    public QuoteService quoteService() {
        return new TransactionalQuoteService(quoteRepository());
    }

    private ImmutableList<Quote> getAllQuotes() {
        long i = 1L;
        ImmutableList.Builder<Quote> quotes = new ImmutableList.Builder<>();
        for (QuoteData qt : SampleData.allQuotes) {
            quotes.add(new Quote(i, qt.text(), qt.attributedTo(), qt.subjects()));
            i += 1;
        }
        return quotes.build();
    }
}
