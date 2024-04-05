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

package eu.cdevreeze.quotes;

import eu.cdevreeze.quotes.repository.QuoteRepository;
import eu.cdevreeze.quotes.service.QuoteService;
import eu.cdevreeze.quotes.web.rest.QuotesAdminRestController;
import eu.cdevreeze.quotes.web.rest.QuotesRestController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Quotes application test.
 *
 * @author Chris de Vreeze
 */
@SpringBootTest
class QuotesApplicationTests implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(QuotesApplicationTests.class);

    private final QuotesRestController quotesRestController;
    private final QuotesAdminRestController quotesAdminRestController;
    private final QuoteService quoteService;
    private final QuoteRepository quoteRepository;

    private ApplicationContext applicationContext;

    @Autowired
    public QuotesApplicationTests(
            QuotesRestController quotesRestController,
            QuotesAdminRestController quotesAdminRestController,
            QuoteService quoteService,
            QuoteRepository quoteRepository) {
        this.quotesRestController = quotesRestController;
        this.quotesAdminRestController = quotesAdminRestController;
        this.quoteService = quoteService;
        this.quoteRepository = quoteRepository;
    }

    @Test
    void contextLoads() {
        assertNotNull(quotesRestController);
        assertNotNull(quotesAdminRestController);
        assertNotNull(quoteService);
        assertNotNull(quoteRepository);

        var handlerMappingMap = applicationContext.getBeansOfType(HandlerMapping.class);
        var handlerAdapterMap = applicationContext.getBeansOfType(HandlerAdapter.class);
        var viewResolverBeanMap = applicationContext.getBeansOfType(ViewResolver.class);
        Map<String, HttpMessageConverter<?>> httpMessageConverterBeanMap =
                applicationContext.getBeansOfType(HttpMessageConverter.class)
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, kv -> (HttpMessageConverter<?>) kv.getValue()));

        for (Map.Entry<String, HandlerMapping> kv : handlerMappingMap.entrySet()) {
            logger.info(String.format("HandlerMapping '%s' of type %s", kv.getKey(), kv.getValue()));
        }
        for (Map.Entry<String, HandlerAdapter> kv : handlerAdapterMap.entrySet()) {
            logger.info(String.format("HandlerAdapter '%s' of type %s", kv.getKey(), kv.getValue()));
        }
        for (Map.Entry<String, ViewResolver> kv : viewResolverBeanMap.entrySet()) {
            logger.info(String.format("View resolver '%s' of type %s", kv.getKey(), kv.getValue()));
        }
        for (Map.Entry<String, HttpMessageConverter<?>> kv : httpMessageConverterBeanMap.entrySet()) {
            logger.info(
                    String.format(
                            "HTTP message converter '%s' of type %s",
                            kv.getKey(),
                            kv.getValue()));
        }
    }

    @Test
    void requestMappingAnnotationRespected() {
        Assertions.assertFalse(applicationContext.getBeansOfType(RequestMappingHandlerMapping.class).isEmpty());
        Assertions.assertFalse(applicationContext.getBeansOfType(RequestMappingHandlerAdapter.class).isEmpty());
    }

    @Test
    void preciselyOneDispatcherServlet() {
        Assertions.assertEquals(1, applicationContext.getBeansOfType(DispatcherServlet.class).size());
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
