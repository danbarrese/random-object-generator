/*
 * Copyright 2016 Dan Barrese
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

import java.lang.reflect.Method;
import java.util.Deque;

/**
 * Generates objects using reflection.
 */
public class IdAwareObjectGenerator extends ObjectGenerator {

    public IdAwareObjectGenerator() {
        super();
    }

    protected <T> void _processMethod(Method method, SetterOverrides setterOverrides, T t,
            Deque<Object> objectStack) throws Exception {
        boolean done = _processCustom(setterOverrides, method, t);
        boolean startsWithSet = method.getName().startsWith("set");
        boolean endsWithIdOrID = method.getName().endsWith("Id") || method.getName().endsWith("ID");
        if (!done) {
            if (startsWithSet && endsWithIdOrID) {
                if (method.getParameterTypes().length == 1) {
                    if (method.getParameterTypes()[0].equals(Long.class) || method.getParameterTypes()[0]
                            .equals(long.class)) {
                        method.invoke(t, nextId(method.getDeclaringClass().getName()));
                        done = true;
                    }
                }
            }
        }
        if (!done) {
            _processNormal(method, setterOverrides, t, objectStack);
        }
    }

}
