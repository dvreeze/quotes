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

import com.google.common.collect.ImmutableMap;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Small utility class to make working with non-persistent collection-backed "databases" a bit easier.
 * Note that what is called a "table" in method names can be any data structure, and not just "flat" ones
 * corresponding to regular database tables.
 *
 * @author Chris de Vreeze
 */
public class MapBasedRepositories {

    private MapBasedRepositories() {
    }

    // Operations on ImmutableMap instances

    public static <K extends Comparable<? super K>, V> Optional<K> findMaxKey(ImmutableMap<K, V> table) {
        return table.keySet().stream().max(Comparator.naturalOrder());
    }

    public static <K, V> ImmutableMap<K, V> addRowToTable(K key, V row, ImmutableMap<K, V> table) {
        return ImmutableMap.<K, V>builder().putAll(table).put(key, row).build();
    }

    public static <K, V> ImmutableMap<K, V> deleteRow(K key, ImmutableMap<K, V> table) {
        return ImmutableMap.<K, V>builder()
                .putAll(
                        table.entrySet().stream()
                                .filter(kv -> !kv.getKey().equals(key))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build();
    }

    public static <V> ImmutableMap<Long, V> addRowToTableGeneratingKey(Function<Long, V> makeRow, ImmutableMap<Long, V> table) {
        var nextId = 1L + MapBasedRepositories.findMaxKey(table).orElse(0L);
        var row = makeRow.apply(nextId);
        return ImmutableMap.<Long, V>builder().putAll(table).put(nextId, row).build();
    }

    public static <V> Optional<V> findLastRow(ImmutableMap<Long, V> table) {
        var lastIdOption = MapBasedRepositories.findMaxKey(table);
        return lastIdOption.map(table::get);
    }

    // Operations on AtomicReferences to ImmutableMap instances

    public static <K, V> ImmutableMap<K, V> addRowToTable(K key, V row, AtomicReference<ImmutableMap<K, V>> table) {
        return table.updateAndGet(tbl -> addRowToTable(key, row, tbl));
    }

    public static <K, V> ImmutableMap<K, V> deleteRow(K key, AtomicReference<ImmutableMap<K, V>> table) {
        return table.updateAndGet(tbl -> deleteRow(key, tbl));
    }

    public static <V> ImmutableMap<Long, V> addRowToTableGeneratingKey(Function<Long, V> makeRow, AtomicReference<ImmutableMap<Long, V>> table) {
        return table.updateAndGet(tbl -> addRowToTableGeneratingKey(makeRow, tbl));
    }
}
