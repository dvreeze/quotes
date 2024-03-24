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

package eu.cdevreeze.quotes.repository.inmemory;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.repository.QuoteRepository;

import java.util.Comparator;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory "implementation" of QuoteRepository, meant to be used in unit tests (of web controllers etc.).
 * It is extremely light-weight, and may make mocking of repositories unnecessary.
 *
 * @author Chris de Vreeze
 */
public class InMemoryQuoteRepository implements QuoteRepository {

    private final AtomicReference<ImmutableList<Quote>> quoteDatabase;

    public InMemoryQuoteRepository(ImmutableList<Quote> initialQuoteDatabaseContent) {
        this.quoteDatabase = new AtomicReference<>(initialQuoteDatabaseContent);
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        return quoteDatabase.get();
    }

    @Override
    public ImmutableList<Quote> findBySubject(String subject) {
        return quoteDatabase.get().stream()
                .filter(qt -> qt.subjects().contains(subject))
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public Quote addQuote(QuoteData quote) {
        var updatedDbContent = quoteDatabase.updateAndGet(db -> {
                    var nextId = db.stream().flatMap(qt -> qt.idOption().stream().boxed())
                            .max(Comparator.naturalOrder()).orElse(1L);
                    var newQuote = new Quote(OptionalLong.of(nextId), quote.text(), quote.attributedTo(), quote.subjects());
                    return ImmutableList.<Quote>builder().addAll(db).add(newQuote).build();
                }
        );
        return updatedDbContent.getLast();
    }
}
