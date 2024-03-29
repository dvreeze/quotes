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

package eu.cdevreeze.quotes.repository.jdbc;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.props.JdbcRepositorySelectionProperties;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * Delegating JDBC-based QuoteRepository.
 *
 * @author Chris de Vreeze
 */
@Repository
public class DelegatingJdbcQuoteRepository implements QuoteRepository {

    private final Logger logger = LoggerFactory.getLogger(DelegatingJdbcQuoteRepository.class);

    private final QuoteRepository delegate;

    public DelegatingJdbcQuoteRepository(JdbcRepositorySelectionProperties props, DataSource dataSource) {
        this.delegate = switch (props.jdbcRepository()) {
            case "JdbcQuoteRepositoryUsingJson" -> new JdbcQuoteRepositoryUsingJson(dataSource);
            case "JdbcQuoteRepositoryUsingOnlyJson" -> new JdbcQuoteRepositoryUsingOnlyJson(dataSource);
            default -> new JdbcQuoteRepository(dataSource);
        };
        logger.info(String.format("Chosen QuoteRepository: %s", this.delegate.getClass()));
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        return delegate.findAllQuotes();
    }

    @Override
    public ImmutableList<Quote> findBySubject(String subject) {
        return delegate.findBySubject(subject);
    }

    @Override
    public Quote addQuote(QuoteData quote) {
        return delegate.addQuote(quote);
    }

    @Override
    public void deleteQuote(long quoteId) {
        delegate.deleteQuote(quoteId);
    }
}
