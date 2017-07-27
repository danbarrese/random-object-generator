/*
 * Copyright 2017 Dan Barrese
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
package com.grepcurl.random;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class SetterOverrides {

    private Map<Class, Map<String, Callable>> overrides = new HashMap<>();
    private Set<String> allMethodNames = new HashSet<>();

    public SetterOverrides add(String setterName, Callable override) {
        return add(Object.class, setterName, override);
    }

    public SetterOverrides add(Class klass, String setterName, Callable override) {
        overrides.computeIfAbsent(klass, k -> new HashMap<>()).put(setterName, override);
        allMethodNames.add(setterName);
        return this;
    }

    public SetterOverrides addAll(Class klass, Map<String, Callable> setterOverrides) {
        if (setterOverrides != null) {
            overrides.computeIfAbsent(klass, k -> new HashMap<>()).putAll(setterOverrides);
            allMethodNames.addAll(setterOverrides.keySet());
        }
        return this;
    }

    public Callable get(Class klass, String setterName) {
        if (overrides.containsKey(klass) && overrides.get(klass).containsKey(setterName)) {
            return overrides.get(klass).get(setterName);
        }
        return null;
    }

    public SetterOverrides reset() {
        overrides.clear();
        allMethodNames.clear();
        return this;
    }

    public Map<Class, Map<String, Callable>> getOverrides() {
        return overrides;
    }

    public Set<String> getAllMethodNames() {
        return allMethodNames;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SetterOverrides{");
        sb.append("overrides=");
        if (overrides != null) {
            overrides.forEach((klass, callablesByMethodName) -> {
                sb.append(klass).append("::");
                sb.append(callablesByMethodName.keySet());
            });
        }
        sb.append('}');
        return sb.toString();
    }

}

