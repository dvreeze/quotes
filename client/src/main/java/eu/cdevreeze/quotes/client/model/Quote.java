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

package eu.cdevreeze.quotes.client.model;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

/**
 * Quote model data class (or DTO). It is deeply immutable and thread-safe. Unlike classes with Lombok annotations,
 * it does not depend on code generation before being complete from the perspective of the reader of the code.
 *
 * @author Chris de Vreeze
 */
public record Quote(
        long id,
        String text,
        String attributedTo,
        ImmutableList<String> subjects
) {
    public Quote {
        Objects.requireNonNull(text);
        Objects.requireNonNull(attributedTo);
        Objects.requireNonNull(subjects);
    }
}
