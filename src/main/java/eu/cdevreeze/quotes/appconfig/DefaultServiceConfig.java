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

import eu.cdevreeze.quotes.props.JdbcRepositoryChoiceProperties;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import eu.cdevreeze.quotes.repository.jdbc.DelegatingJdbcQuoteRepository;
import eu.cdevreeze.quotes.service.QuoteService;
import eu.cdevreeze.quotes.service.impl.TransactionalQuoteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Default Spring Configuration for service and repository layer.
 *
 * @author Chris de Vreeze
 */
@Configuration
@ConditionalOnProperty(value = "useNonPersistentRepositories", havingValue = "false", matchIfMissing = true)
public class DefaultServiceConfig implements ServiceConfigApi {

    private final JdbcRepositoryChoiceProperties props;
    private final DataSource dataSource;

    public DefaultServiceConfig(JdbcRepositoryChoiceProperties props, DataSource dataSource) {
        this.props = props;
        this.dataSource = dataSource;
    }

    @Bean
    public QuoteRepository quoteRepository() {
        return new DelegatingJdbcQuoteRepository(props, dataSource);
    }

    @Bean
    public QuoteService quoteService() {
        return new TransactionalQuoteService(quoteRepository());
    }
}
