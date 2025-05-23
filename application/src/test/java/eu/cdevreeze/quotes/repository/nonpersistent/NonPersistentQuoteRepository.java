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
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import eu.cdevreeze.quotes.sampledata.SampleData;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
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

    public NonPersistentQuoteRepository() {
        this(getAllQuotes());
    }

    public void reset(ImmutableMap<Long, Quote> initialQuoteDatabaseContent) {
        this.quoteDatabase.set(initialQuoteDatabaseContent);
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
    public ImmutableList<Quote> findByAttributedTo(String attributedTo) {
        return findAllQuotes().stream()
                .filter(qt -> qt.attributedTo().equals(attributedTo))
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public Quote addQuote(QuoteData quoteData) {
        AtomicReference<Quote> quoteRef = new AtomicReference<>();
        quoteDatabase.updateAndGet(db -> {
            long nextId = 1L + db.keySet().stream().max(Comparator.naturalOrder()).orElse(0L);
            Quote quote = new Quote(nextId, quoteData.text(), quoteData.attributedTo(), quoteData.subjects());
            quoteRef.set(quote);
            Map<Long, Quote> tempDb = new HashMap<>(db);
            tempDb.put(nextId, quote);
            return ImmutableMap.copyOf(tempDb);
        });
        return quoteRef.get();
    }

    @Override
    public void deleteQuote(long quoteId) {
        quoteDatabase.updateAndGet(db ->
                db.entrySet()
                        .stream()
                        .filter(kv -> kv.getKey() != quoteId)
                        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    private static ImmutableMap<Long, Quote> getAllQuotes() {
        long i = 1L;
        ImmutableMap.Builder<Long, Quote> quotes = new ImmutableMap.Builder<>();
        for (QuoteData qt : SampleData.allQuotes) {
            quotes.put(i, new Quote(i, qt.text(), qt.attributedTo(), qt.subjects()));
            i += 1;
        }
        return quotes.build();
    }
}
