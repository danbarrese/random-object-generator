/*
 * Copyright 2025 Dan Barrese
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.danbarrese.random.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class FieldOverrides {

    private final Map<Class<?>, Callable<?>> typeOverrides = new HashMap<>();
    private final Map<Class<?>, Map<String, Callable<?>>> fieldOverrides = new HashMap<>();

    // -------------------------------
    // public methods
    // -------------------------------

    public FieldOverrides add(String fieldName, Callable<?> override) {
        return add(Object.class, fieldName, override);
    }

    public FieldOverrides add(Class<?> klass, String fieldName, Callable<?> override) {
        fieldOverrides.computeIfAbsent(klass, k -> new HashMap<>()).put(fieldName, override);
        return this;
    }

    public FieldOverrides add(Class<?> klass, Callable<?> override) {
        typeOverrides.put(klass, override);
        return this;
    }

    public FieldOverrides addAll(Class<?> klass, Map<String, Callable<?>> fieldOverrides) {
        if (fieldOverrides != null) {
            this.fieldOverrides.computeIfAbsent(klass, k -> new HashMap<>()).putAll(fieldOverrides);
        }
        return this;
    }

    public Callable<?> get(Class<?> klass, String fieldName) {
        if (fieldOverrides.containsKey(klass) && fieldOverrides.get(klass).containsKey(fieldName)) {
            return fieldOverrides.get(klass).get(fieldName);
        }
        return null;
    }

    public FieldOverrides reset() {
        typeOverrides.clear();
        fieldOverrides.clear();
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FieldOverrides{");
        sb.append("fieldOverrides=");
        fieldOverrides.forEach((klass, callablesByFieldName) -> {
            sb.append(klass).append("::");
            sb.append(callablesByFieldName.keySet());
        });
        sb.append('}');
        return sb.toString();
    }

}

