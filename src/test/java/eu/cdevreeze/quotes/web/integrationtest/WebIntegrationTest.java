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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer integration test, mocking the HTTP server using MockMvc, and taking a NonPersistentQuoteRepository
 * as QuoteRepository.
 *
 * @author Chris de Vreeze
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = {"/test-repository-overrides.properties"})
class WebIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    public WebIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void shouldReturnAllQuotes() throws Exception {
        this.mockMvc.perform(get("/quotes.json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(Matchers.containsString("Ron Paul")))
                .andExpect(content().string(Matchers.containsString("Wim Hof")))
                .andExpect(content().string(Matchers.containsString("Isaac Newton")))
                .andExpect(content().string(Matchers.containsString("Smedley Butler")))
                .andExpect(content().string(Matchers.containsString("Henry Kissinger")))
                .andExpect(content().string(Matchers.containsString("Nikola Tesla")))
                .andExpect(content().string(Matchers.containsString("Ben Rich")));
    }
}
