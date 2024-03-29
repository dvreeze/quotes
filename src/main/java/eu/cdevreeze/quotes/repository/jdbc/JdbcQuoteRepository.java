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
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * (Spring) JDBC-based QuoteRepository implementation.
 *
 * @author Chris de Vreeze
 */
@Repository
public class JdbcQuoteRepository implements QuoteRepository {

    private final JdbcClient jdbcClient;

    public JdbcQuoteRepository(DataSource dataSource) {
        this.jdbcClient = JdbcClient.create(dataSource);
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        var stmt = jdbcClient.sql(findQuotesBaseSql);
        return findQuotes(stmt);
    }

    @Override
    public ImmutableList<Quote> findBySubject(String subject) {
        String sql = String.format("%s%n", findQuotesBaseSql) + """
                where sub.subject = :subject""";

        var stmt = jdbcClient.sql(sql).param("subject", subject);
        return findQuotes(stmt);
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

    // Nice reuse across select queries
    // Note the "stream" call only on a retrieved collection, to prevent having to close the stream
    private ImmutableList<Quote> findQuotes(JdbcClient.StatementSpec stmt) {
        return stmt
                .query(this::mapRow)
                .list()
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
                                    grp.stream().flatMap(row -> row.subjectOption().stream()).collect(ImmutableList.toImmutableList())
                            );
                        }
                )
                .collect(ImmutableList.toImmutableList());
    }

    private QuoteSubjectRow mapRow(ResultSet rs, int rowNum) {
        try {
            return new QuoteSubjectRow(
                    rs.getLong("id"),
                    rs.getString("text"),
                    rs.getString("attributedTo"),
                    Optional.ofNullable(rs.getString("subject"))
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    record QuoteSubjectRow(long id, String text, String attributedTo, Optional<String> subjectOption) {
    }
}
