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

package eu.cdevreeze.quotes.service.impl;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import eu.cdevreeze.quotes.service.QuoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional quotes service implementation.
 *
 * @author Chris de Vreeze
 */
@Service
public class TransactionalQuoteService implements QuoteService {

    private final QuoteRepository quoteRepository;

    public TransactionalQuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ImmutableList<Quote> findAllQuotes() {
        return quoteRepository.findAllQuotes();
    }

    @Override
    @Transactional(readOnly = true)
    public ImmutableList<Quote> findBySubject(String subject) {
        return quoteRepository.findBySubject(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public ImmutableList<Quote> findByAttributedTo(String attributedTo) {
        return quoteRepository.findByAttributedTo(attributedTo);
    }

    @Override
    @Transactional
    public Quote addQuote(QuoteData quote) {
        return quoteRepository.addQuote(quote);
    }

    @Override
    @Transactional
    public void deleteQuote(long quoteId) {
        quoteRepository.deleteQuote(quoteId);
    }
}
