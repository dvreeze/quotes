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

package eu.cdevreeze.quotes.repository.nonpersistent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.quotes.internal.utils.MapBasedRepositories;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.repository.QuoteRepository;

import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory "implementation" of QuoteRepository, meant to be used in unit tests (of web controllers etc.).
 * It is extremely light-weight, and may make mocking of repositories unnecessary.
 *
 * @author Chris de Vreeze
 */
public class NonPersistentQuoteRepository implements QuoteRepository {

    private final AtomicReference<ImmutableMap<Long, Quote>> quoteDatabase;

    public NonPersistentQuoteRepository(ImmutableMap<Long, Quote> initialQuoteDatabaseContent) {
        this.quoteDatabase = new AtomicReference<>(initialQuoteDatabaseContent);
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        return quoteDatabase.get().values().stream().collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<Quote> findBySubject(String subject) {
        return findAllQuotes().stream()
                .filter(qt -> qt.subjects().contains(subject))
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public Quote addQuote(QuoteData quote) {
        var updatedDbContent = MapBasedRepositories.addRowToTableGeneratingKey(
                id -> new Quote(id, quote.text(), quote.attributedTo(), quote.subjects()),
                quoteDatabase
        );
        return MapBasedRepositories.findLastRow(updatedDbContent).orElseThrow();
    }

    @Override
    public void deleteQuote(long quoteId) {
        MapBasedRepositories.deleteRow(quoteId, quoteDatabase);
    }
}
