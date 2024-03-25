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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * (Spring) JDBC-based QuoteRepository implementation that uses (almost) only JSON in the query result sets.
 *
 * @author Chris de Vreeze
 */
@Repository
@Primary
public class JdbcQuoteRepositoryUsingOnlyJson implements QuoteRepository {

    private final JdbcClient jdbcClient;

    public JdbcQuoteRepositoryUsingOnlyJson(DataSource dataSource) {
        this.jdbcClient = JdbcClient.create(dataSource);
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        String sql = """
                select qt.id,
                       json_object('text', qt.text, 'attributedTo', qt.attributedTo, 'subjects', json_arrayagg(subj.subject))
                           as quote
                  from quote qt
                  left join quote_subject subj on qt.id = subj.quote_id
                 group by qt.id""";
        var stmt = jdbcClient.sql(sql);
        return findQuotes(stmt);
    }

    @Override
    public ImmutableList<Quote> findBySubject(String subject) {
        // Inefficient. Improve the SQL instead to limit the result set.
        return findAllQuotes().stream()
                .filter(qt -> qt.subjects().contains(subject)).collect(ImmutableList.toImmutableList());
    }

    @Override
    public Quote addQuote(QuoteData quote) {
        var quoteId = addQuoteWithoutSubjects(quote);
        var quoteWithId =
                new Quote(OptionalLong.of(quoteId), quote.text(), quote.attributedTo(), quote.subjects());
        addQuoteSubjects(quoteWithId);
        return quoteWithId;
    }

    // Nice reuse across select queries
    // Note the "stream" call only on a retrieved collection, to prevent having to close the stream
    private ImmutableList<Quote> findQuotes(JdbcClient.StatementSpec stmt) {
        var objectMapper = getObjectMapper();
        return stmt
                .query((ResultSet rs, int rowNum) -> mapRow(rs, objectMapper))
                .list()
                .stream()
                .collect(ImmutableList.toImmutableList());
    }

    private Quote mapRow(ResultSet rs, ObjectMapper objectMapper) {
        try {
            var quoteId = OptionalLong.of(rs.getLong("id"));
            var jsonString = rs.getString("quote");
            var quoteData = objectMapper.readValue(jsonString, QuoteData.class);
            return new Quote(quoteId, quoteData.text(), quoteData.attributedTo(), quoteData.subjects());
        } catch (SQLException | IOException e) {
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
                .param("quote_id", quote.idOption().orElseThrow())
                .param("subject", subject)
                .update());
    }

    private ObjectMapper getObjectMapper() {
        return JsonMapper.builder().addModule(new Jdk8Module()).addModule(new GuavaModule()).build();
    }
}