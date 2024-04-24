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

package eu.cdevreeze.quotes.web.integrationtest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.quotes.internal.utils.ObjectMappers;
import eu.cdevreeze.quotes.model.Quote;
import eu.cdevreeze.quotes.model.QuoteData;
import eu.cdevreeze.quotes.model.SampleData;
import eu.cdevreeze.quotes.repository.QuoteRepository;
import eu.cdevreeze.quotes.repository.nonpersistent.NonPersistentQuoteRepository;
import eu.cdevreeze.quotes.service.QuoteService;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer integration test, mocking the HTTP server using MockMvc, and taking a NonPersistentQuoteRepository
 * as QuoteRepository.
 *
 * @author Chris de Vreeze
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@TestPropertySource(locations = {"classpath:test-repository-overrides.properties"})
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebIntegrationTest {

    private final MockMvc mockMvc;
    private final QuoteService quoteService;
    private final QuoteRepository quoteRepository;

    private ImmutableMap<Long, Quote> initialDatabaseContent = ImmutableMap.of();

    @Autowired
    public WebIntegrationTest(MockMvc mockMvc, QuoteService quoteService, QuoteRepository quoteRepository) {
        this.mockMvc = mockMvc;
        this.quoteService = quoteService;
        this.quoteRepository = quoteRepository;
    }

    @BeforeAll
    void initialise() {
        initialDatabaseContent =
                quoteRepository.findAllQuotes().stream()
                        .collect(ImmutableMap.toImmutableMap(Quote::id, qt -> qt));
    }

    @BeforeEach
    void reloadDatabase() {
        ((NonPersistentQuoteRepository) quoteRepository).reset(initialDatabaseContent);
    }

    @Test
    void shouldReturnAllQuotes() throws Exception {
        this.mockMvc.perform(get("/quotes.json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].attributedTo", Matchers.equalTo("Wim Hof")))
                .andExpect(jsonPath("$[0].subjects[0]", Matchers.equalTo("inner strength")))
                .andExpect(jsonPath("$[4].attributedTo", Matchers.equalTo("Wim Hof")))
                .andExpect(jsonPath("$[4].subjects[0]", Matchers.equalTo("inner strength")))
                .andExpect(jsonPath("$[10].attributedTo", Matchers.equalTo("Ron Paul")))
                .andExpect(jsonPath("$[10].subjects[0]", Matchers.equalTo("defense")))
                .andExpect(jsonPath("$[10].text",
                        Matchers.equalTo("Legitimate use of violence can only be that which is required in self-defense.")))
                .andExpect(jsonPath("$[18].attributedTo", Matchers.equalTo("Isaac Newton")))
                .andExpect(jsonPath("$[18].subjects[0]", Matchers.equalTo("genius")))
                .andExpect(jsonPath("$[18].text", Matchers.equalTo("Genius is patience")))
                .andExpect(jsonPath("$[22].attributedTo", Matchers.equalTo("Smedley Butler")))
                .andExpect(jsonPath("$[22].subjects[0]", Matchers.equalTo("war")))
                .andExpect(jsonPath("$[24].attributedTo", Matchers.equalTo("Henry Kissinger")))
                .andExpect(jsonPath("$[24].subjects[0]", Matchers.equalTo("corrupt government")))
                .andExpect(jsonPath("$[24].text",
                        Matchers.equalTo("The illegal we do immediately; the unconstitutional takes a little longer.")))
                .andExpect(jsonPath("$[27].attributedTo", Matchers.equalTo("Ben Rich")))
                .andExpect(jsonPath("$[27].subjects[0]", Matchers.equalTo("hidden knowledge")))
                .andExpect(jsonPath("$[27].text",
                        Matchers.equalTo("We now have the technology to bring ET home.")))
                .andExpect(jsonPath("$[28].attributedTo", Matchers.equalTo("Nikola Tesla")))
                .andExpect(jsonPath("$[28].subjects[0]", Matchers.equalTo("hidden knowledge")))
                .andExpect(jsonPath("$[29].attributedTo", Matchers.equalTo("Nikola Tesla")))
                .andExpect(jsonPath("$[29].subjects[0]", Matchers.equalTo("hidden knowledge")));
    }

    @Test
    void shouldReturnQuotesBySubject() throws Exception {
        var subject = "peace";
        this.mockMvc.perform(get("/quotesBySubject.json").param("subject", subject))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].attributedTo", Matchers.equalTo("Ron Paul")))
                .andExpect(jsonPath("$[0].subjects[0]", Matchers.equalTo("liberty")))
                .andExpect(jsonPath("$[0].subjects[1]", Matchers.equalTo("peace")))
                .andExpect(jsonPath("$[1].attributedTo", Matchers.equalTo("Ron Paul")))
                .andExpect(jsonPath("$[1].subjects[0]", Matchers.equalTo("liberty")))
                .andExpect(jsonPath("$[1].subjects[1]", Matchers.equalTo("peace")));
    }

    @Test
    void shouldReturnQuotesByAttributedTo() throws Exception {
        var attributedTo = "Wim Hof";
        this.mockMvc.perform(get("/quotesByAttributedTo.json").param("attributedTo", attributedTo))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].attributedTo", Matchers.equalTo("Wim Hof")))
                .andExpect(jsonPath("$[0].subjects[0]", Matchers.equalTo("inner strength")))
                .andExpect(jsonPath("$[1].attributedTo", Matchers.equalTo("Wim Hof")))
                .andExpect(jsonPath("$[1].subjects[0]", Matchers.equalTo("inner strength")))
                .andExpect(jsonPath("$[2].attributedTo", Matchers.equalTo("Wim Hof")))
                .andExpect(jsonPath("$[2].subjects[0]", Matchers.equalTo("inner strength")))
                .andExpect(jsonPath("$[3].attributedTo", Matchers.equalTo("Wim Hof")))
                .andExpect(jsonPath("$[3].subjects[0]", Matchers.equalTo("inner strength")))
                .andExpect(jsonPath("$[4].attributedTo", Matchers.equalTo("Wim Hof")))
                .andExpect(jsonPath("$[4].subjects[0]", Matchers.equalTo("inner strength")));
    }

    @Test
    void shouldReturnRandomQuote() throws Exception {
        List<Matcher<? super String>> attributedToMatchers = SampleData.allQuotes.stream()
                .map(qt -> (Matcher<? super String>) Matchers.equalTo(qt.attributedTo())).collect(Collectors.toList());
        Matcher<? super String> anyAttributedToMatcher = Matchers.anyOf(attributedToMatchers);

        List<Matcher<? super String>> subjectMatchers = SampleData.allQuotes.stream()
                .map(qt -> (Matcher<? super String>) Matchers.equalTo(qt.subjects().getFirst())).collect(Collectors.toList());
        Matcher<? super String> anySubjectMatcher = Matchers.anyOf(subjectMatchers);

        this.mockMvc.perform(get("/randomQuote.json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.attributedTo").isString())
                .andExpect(jsonPath("$.text").isString())
                .andExpect(jsonPath("$.subjects").isArray())
                .andExpect(jsonPath("$.subjects[0]").isString())
                .andExpect(jsonPath("$.attributedTo").value(anyAttributedToMatcher))
                .andExpect(jsonPath("$.subjects[0]").value(anySubjectMatcher));
    }

    @Test
    void shouldAddQuote() throws Exception {
        var numberOfQuotes = quoteService.findAllQuotes().size();

        var quoteText =
                "We'll know our disinformation program is complete when everything the American public believes is false.";
        var quoteData = new QuoteData(quoteText, "William Casey", ImmutableList.of("corrupt government"));
        var objectMapper = ObjectMappers.getObjectMapper();
        var jsonRequestPayload = objectMapper.writer().writeValueAsString(quoteData);

        this.mockMvc.perform(
                        post("/quote")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequestPayload)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributedTo", Matchers.equalTo(quoteData.attributedTo())))
                .andExpect(jsonPath("$.text", Matchers.equalTo(quoteData.text())))
                .andExpect(jsonPath("$.subjects[0]", Matchers.equalTo(quoteData.subjects().getFirst())));

        var newNumberOfQuotes = quoteService.findAllQuotes().size();

        Assertions.assertEquals(newNumberOfQuotes, numberOfQuotes + 1);
    }

    @Test
    void shouldDeleteQuote() throws Exception {
        var numberOfQuotes = quoteService.findAllQuotes().size();

        var quoteId = 27L; // Quote by Michael Ledeen

        this.mockMvc.perform(delete("/quotes/" + quoteId))
                .andDo(print())
                .andExpect(status().isOk());

        var newNumberOfQuotes = quoteService.findAllQuotes().size();

        Assertions.assertEquals(newNumberOfQuotes, numberOfQuotes - 1);
    }
}
