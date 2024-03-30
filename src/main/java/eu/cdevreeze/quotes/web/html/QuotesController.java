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

package eu.cdevreeze.quotes.web.html;

import eu.cdevreeze.quotes.service.QuoteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Quotes web controller (for use in the HTML site).
 *
 * @author Chris de Vreeze
 */
@Controller
public class QuotesController {

    private final QuoteService quoteService;

    public QuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping(value = "quotes.html")
    public ModelAndView quotes() {
        var modelAndView = new ModelAndView("quotes.html");
        modelAndView.addObject("quotes", quoteService.findAllQuotes());
        return modelAndView;
    }
}
