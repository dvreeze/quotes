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

package eu.cdevreeze.quotes.internal.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * Utility class to get a Jackson ObjectMapper that is aware of Java 8 and Guava collections.
 *
 * @author Chris de Vreeze
 */
public class ObjectMappers {

    private ObjectMappers() {
    }

    public static ObjectMapper getObjectMapper(boolean indentOutput) {
        var resultWithoutPrettifying =
                JsonMapper.builder()
                        .addModule(new Jdk8Module())
                        .addModule(new GuavaModule())
                        .build();

        return (indentOutput) ?
                resultWithoutPrettifying.enable(SerializationFeature.INDENT_OUTPUT) :
                resultWithoutPrettifying;
    }

    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(true);
    }
}
