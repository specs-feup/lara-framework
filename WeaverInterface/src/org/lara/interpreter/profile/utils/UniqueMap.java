/**
 * Copyright 2018 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.profile.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class UniqueMap<K, V> {
    public Map<K, Set<V>> map;

    private Supplier<Set<V>> comparator;

    public UniqueMap() {
        this.map = new HashMap<>();
        this.setComparator(() -> new HashSet<>());
    }

    public UniqueMap(Supplier<Set<V>> supplier) {
        this.map = new HashMap<>();
        this.setComparator(supplier);
    }

    public long getTotalNodes() {
        Optional<Integer> reduce = map.values().stream().map(Set::size)
                .reduce((acc, curr) -> acc + curr);
        return reduce.orElse(0);
    }

    public void put(K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, comparator.get());
        }
        map.get(key).add(value);
    }

    public void setComparator(Supplier<Set<V>> setSupplier) {
        this.comparator = setSupplier;
    }

    // @Override
    // public String toString() {
    // StringBuilder builder = new StringBuilder();
    // for (String strKey : map.keySet()) {
    // builder.append(strKey + ": {");
    // for (Object key : map.get(strKey)) {
    // if (key instanceof CtNamedElement) {
    // builder.append("\t" + key.getClass() + "#" + ((CtNamedElement) key).getSimpleName() + "@"
    // + key.hashCode() + "\n");
    // } else if (key instanceof CtElement) {
    // builder.append("\t" + key.getClass() + "@" + key.hashCode() + "\n");
    // } else {
    // builder.append("\t" + key + "@" + key.hashCode() + "\n");
    // }
    // }
    // builder.append("}");
    // }
    // return builder.toString();
    // }
}