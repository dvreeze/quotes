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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * (Spring) JDBC-based QuoteRepository implementation.
 *
 * @author Chris de Vreeze
 */
@Repository
@ConditionalOnProperty(
        name = "implementation.jdbcQuoteRepository",
        havingValue = "JdbcQuoteRepository"
)
public class JdbcQuoteRepository implements QuoteRepository {

    private final JdbcClient jdbcClient;

    public JdbcQuoteRepository(DataSource dataSource) {
        this.jdbcClient = JdbcClient.create(dataSource);
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        List<QuoteSubjectRow> rows = jdbcClient.sql(findQuotesBaseSql).query(QuoteSubjectRow.class).list();
        return extractQuotes(rows);
    }

    @Override
    public ImmutableList<Quote> findBySubject(String subject) {
        String sql = String.format("%s%n", findQuotesBaseSql) + """
                where sub.subject = :subject""";

        List<QuoteSubjectRow> rows = jdbcClient
                .sql(sql)
                .param("subject", subject)
                .query(QuoteSubjectRow.class)
                .list();
        return extractQuotes(rows);
    }

    @Override
    public ImmutableList<Quote> findByAttributedTo(String attributedTo) {
        String sql = String.format("%s%n", findQuotesBaseSql) + """
                where qt.attributedTo = :attributedTo""";

        List<QuoteSubjectRow> rows = jdbcClient
                .sql(sql)
                .param("attributedTo", attributedTo)
                .query(QuoteSubjectRow.class)
                .list();
        return extractQuotes(rows);
    }

    @Override
    public Quote addQuote(QuoteData quote) {
        var quoteId = addQuoteWithoutSubjects(quote);
        var quoteWithId =
                new Quote(quoteId, quote.text(), quote.attributedTo(), quote.subjects());
        addQuoteSubjects(quoteWithId);
        return quoteWithId;
    }

    @Override
    public void deleteQuote(long quoteId) {
        deleteQuoteSubjects(quoteId);
        deleteQuoteWithoutSubjects(quoteId);
    }

    private ImmutableList<Quote> extractQuotes(List<QuoteSubjectRow> rows) {
        return rows
                .stream()
                .collect(Collectors.groupingBy(QuoteSubjectRow::id))
                .values()
                .stream()
                .map(grp -> {
                            var first = grp.getFirst();
                            return new Quote(
                                    first.id(),
                                    first.text(),
                                    first.attributedTo(),
                                    grp.stream()
                                            .flatMap(row -> row.subject().stream())
                                            .collect(ImmutableList.toImmutableList())
                            );
                        }
                )
                .collect(ImmutableList.toImmutableList());
    }

    private long addQuoteWithoutSubjects(QuoteData quote) {
        String sql = """
                insert into quote (text, attributedTo)
                values (:text, :attributedTo)""";
        var keyHolder = new GeneratedKeyHolder();
        var insertCount = jdbcClient.sql(sql)
                .param("text", quote.text())
                .param("attributedTo", quote.attributedTo())
                .update(keyHolder);
        Preconditions.checkArgument(insertCount >= 1);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void addQuoteSubjects(Quote quote) {
        String sql = "insert into quote_subject (quote_id, subject) values (:quote_id, :subject)";
        quote.subjects().forEach(subject -> jdbcClient.sql(sql)
                .param("quote_id", quote.id())
                .param("subject", subject)
                .update());
    }

    private void deleteQuoteWithoutSubjects(long quoteId) {
        String sql = "delete from quote where id = :quote_id";
        jdbcClient.sql(sql).param("quote_id", quoteId).update();
    }

    private void deleteQuoteSubjects(long quoteId) {
        String sql = "delete from quote_subject where quote_id = :quote_id";
        jdbcClient.sql(sql).param("quote_id", quoteId).update();
    }

    private final String findQuotesBaseSql = """
            select qt.id, qt.text, qt.attributedTo, subj.subject
              from quote qt
              left join quote_subject subj on qt.id = subj.quote_id""";

    record QuoteSubjectRow(long id, String text, String attributedTo, Optional<String> subject) {
    }
}
