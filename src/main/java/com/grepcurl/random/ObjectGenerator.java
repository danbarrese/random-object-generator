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

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Generates objects using reflection.
 */
@SuppressWarnings({ "unchecked" })
public class ObjectGenerator extends BaseGenerator {

    public int DEFAULT_SET_SIZE_MIN = 1;
    public int DEFAULT_SET_SIZE_MAX = 10;
    public SetterOverrides defaultSetterOverrides = new SetterOverrides();

    public ObjectGenerator() {
    }

    public void addSetterOverride(String setterName, Callable override) {
        defaultSetterOverrides.add(setterName, override);
    }

    public void addSetterOverride(Class klass, String setterName, Callable override) {
        defaultSetterOverrides.add(klass, setterName, override);
    }

    public void addSetterOverrides(Class klass, Map<String, Callable> setterOverrides) {
        defaultSetterOverrides.addAll(klass, setterOverrides);
    }

    public void resetSetterOverrides() {
        defaultSetterOverrides.reset();
    }

    public <T> T generate(Class<T> klass) {
        Validate.notNull(klass);
        if (verbose) {
            log(String.format("generating object of type: %s", klass));
        }
        try {
            Deque<Object> objectStack = new ArrayDeque<>();
            T t;
            if (klass.isEnum()) {
                int randomOrdinal = randomInt(0, klass.getEnumConstants().length - 1);
                t = klass.getEnumConstants()[randomOrdinal];
            }
            else {
                t = klass.getConstructor().newInstance();
            }
            objectStack.push(t);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                _processMethod(method, null, t, objectStack);
            }
            objectStack.pop();
            return t;
        }
        catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    @SuppressWarnings("unused")
    public <T> T generate(Class<T> klass, Object... constructorArgs) {
        Validate.notNull(klass);
        Validate.notNull(constructorArgs);
        if (verbose) {
            log(String.format("generating object of type: %s, with args: %s", klass, Arrays.toString(constructorArgs)));
        }
        try {
            Deque<Object> objectStack = new ArrayDeque<>();
            Class[] constructorTypes = _toClasses(constructorArgs);
            T t;
            if (klass.isEnum()) {
                int randomOrdinal = randomInt(0, klass.getEnumConstants().length - 1);
                t = klass.getEnumConstants()[randomOrdinal];
            }
            else {
                t = klass.getConstructor(constructorTypes).newInstance(constructorArgs);
            }
            objectStack.push(t);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                _processMethod(method, new SetterOverrides(), t, objectStack);
            }
            objectStack.pop();
            return t;
        }
        catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    protected <T> T generate(Class<T> klass, SetterOverrides setterOverrides,
            Deque<Object> objectStack, Object... constructorArgs) {
        Validate.notNull(klass);
        Validate.notNull(constructorArgs);
        if (verbose) {
            log(String.format("generating object of type: %s, with args: %s, with overrides: %s", klass,
                    Arrays.toString(constructorArgs),
                    setterOverrides));
        }
        try {
            Class[] constructorTypes = _toClasses(constructorArgs);
            T t;
            if (klass.isEnum()) {
                int randomOrdinal = randomInt(0, klass.getEnumConstants().length - 1);
                t = klass.getEnumConstants()[randomOrdinal];
            }
            else {
                t = klass.getConstructor(constructorTypes).newInstance(constructorArgs);
            }
            objectStack.push(t);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                _processMethod(method, setterOverrides, t, objectStack);
            }
            objectStack.pop();
            return t;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    @Deprecated
    public <T> T generate(Class<T> klass, Map<String, Callable> setterOverrides, Object... constructorArgs) {
        SetterOverrides o = new SetterOverrides();
        if (setterOverrides != null) {
            setterOverrides.forEach((methodName, callable) -> o.add(methodName, callable));
        }
        return generate(klass, o, constructorArgs);
    }

    public <T> T generate(Class<T> klass, SetterOverrides setterOverrides, Object... constructorArgs) {
        return generate(klass, setterOverrides, new ArrayDeque<>(), constructorArgs);
    }

    public <T> T generate(Class<T> klass, SetterOverrides setterOverrides, String[] constructorArgTypes,
            Object... constructorArgs) {
        Validate.notNull(klass);
        Validate.notNull(constructorArgs);
        if (verbose) {
            log(String.format("generating object of type: %s, with args: %s, of types: %s, with overrides: %s", klass,
                    Arrays.toString(constructorArgs),
                    Arrays.toString(constructorArgTypes),
                    setterOverrides));
        }
        try {
            Deque<Object> objectStack = new ArrayDeque<>();
            Class[] constructorTypes = _toClasses(constructorArgTypes, constructorArgs);
            T t = klass.getConstructor(constructorTypes).newInstance(constructorArgs);
            objectStack.push(t);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                _processMethod(method, setterOverrides, t, objectStack);
            }
            objectStack.pop();
            return t;
        }
        catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    public <T, E> Collection<T> randomCollection(Type elementType, Class<E> collectionType, int count) {
        return randomCollection(elementType, collectionType, new SetterOverrides(), count, new ArrayDeque<>());
    }

    // -----------------------
    // protected methods
    // -----------------------

    protected <T, E> Collection<T> randomCollection(Type elementType,
            Class<E> collectionType,
            SetterOverrides setterOverrides,
            int count,
            Deque<Object> objectStack) {
        Validate.isTrue(count >= 0);
        Validate.notNull(elementType);
        Validate.notNull(collectionType);
        if (verbose) {
            log(String.format("generating %s<%s> (%d)", collectionType, elementType, count));
        }
        Collection<T> collection;
        if (collectionType.equals(List.class)) {
            collection = new ArrayList<>();
        }
        else if (collectionType.equals(Set.class)) {
            collection = new HashSet<>();
        }
        else if (collectionType.equals(Deque.class)) {
            collection = new ArrayDeque<>();
        }
        else if (collectionType.equals(Collection.class)) {
            collection = new ArrayList<>();
        }
        else {
            throw new IllegalArgumentException(
                    "Don't know how to generate a random collection of type: " + collectionType.getName());
        }
        for (int i = 0; i < count; i++) {
            collection.add((T) _process(elementType, setterOverrides, objectStack));
        }
        return collection;
    }

    protected <T, S, E> Map<T, S> randomMap(Type[] elementTypes,
            Class<E> collectionType,
            SetterOverrides setterOverrides,
            int count,
            Deque<Object> objectStack) {
        Validate.isTrue(count >= 0);
        Validate.notNull(elementTypes);
        Validate.notNull(collectionType);
        Map<T, S> collection;
        if (collectionType.equals(Map.class)) {
            collection = new HashMap<>();
        }
        else {
            throw new IllegalArgumentException(
                    "Don't know how to generate a random multi-param collection of type: " + collectionType.getName());
        }
        for (int i = 0; i < count; i++) {
            collection.put((T) _process(elementTypes[0], setterOverrides, objectStack),
                    (S) _process(elementTypes[1], setterOverrides, objectStack));
        }
        return collection;
    }

    protected <T> void _processMethod(Method method, SetterOverrides setterOverrides, T t,
            Deque<Object> objectStack) throws Exception {
        boolean done = _processCustom(setterOverrides, method, t);
        if (!done) {
            _processNormal(method, setterOverrides, t, objectStack);
        }
    }

    protected Object _process(Type type, SetterOverrides setterOverrides) {
        return _process(type, setterOverrides, new ArrayDeque<>());
    }

    protected Object _process(Type type, SetterOverrides setterOverrides, Deque<Object> objectStack) {
        Class<?> klass = _toClass(type);
        if (type instanceof ParameterizedType) {
            if (_getParameterTypeCount(type) == 1) {
                Type parameterType = _getParameterType((ParameterizedType) type);
                return randomCollection(parameterType,
                        klass,
                        setterOverrides,
                        randomInt(DEFAULT_SET_SIZE_MIN, DEFAULT_SET_SIZE_MAX),
                        objectStack);
            }
            else {
                // assume we want a Map... for now..
                // TODO: what else could we support here?  Say MyObject<One, Two>?
                Type[] parameterTypes = _getParameterTypes(type);
                return randomMap(parameterTypes,
                        klass,
                        setterOverrides,
                        randomInt(DEFAULT_SET_SIZE_MIN, DEFAULT_SET_SIZE_MAX),
                        objectStack);
            }
        }
        if (isBaseType(klass)) {
            return random(klass);
        }
        if (_getObjectByType(objectStack, type) != null) {
            return _getObjectByType(objectStack, type);
        }
        else {
            return generate(klass, setterOverrides, objectStack);
        }
    }

    protected Object _getObjectByType(Deque<Object> objectStack, Type type) {
        Class klass = _toClass(type);
        for (Object o : objectStack) {
            if (o.getClass().equals(klass)) {
                return o;
            }
        }
        return null;
    }

    protected <T> boolean _processNormal(Method method, SetterOverrides setterOverrides, T t,
            Deque<Object> objectStack) throws InvocationTargetException, IllegalAccessException {
        if (method.getName().startsWith("set") && method.getName().length() > 3) {
            Type[] types = method.getGenericParameterTypes();

            Object[] params = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                params[i] = _process(types[i], setterOverrides, objectStack);
            }
            method.invoke(t, params);
            return true;
        }
        else {
            return false;
        }
    }

    @SuppressWarnings("RedundantCast")
    protected <T> boolean _processCustom(SetterOverrides setterOverrides, Method method, T t)
            throws Exception {
        if (setterOverrides != null || defaultSetterOverrides != null) {
            Class<?> klass = t.getClass();
            if (_processCustom(setterOverrides, klass, method, t)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("RedundantCast")
    protected <T> boolean _processCustom(SetterOverrides setterOverrides,
            Class<?> classUsedForOverrides,
            Method method,
            T t) throws Exception {
        if (setterOverrides != null) {
            Callable c = setterOverrides.get(classUsedForOverrides, method.getName());
            if (c != null) {
                Object params = c.call();
                method.invoke(t, params == null ? (Object) null : params);
                return true;
            }
        }

        if (defaultSetterOverrides != null) {
            Callable c = defaultSetterOverrides.get(classUsedForOverrides, method.getName());
            if (c != null) {
                Object params = c.call();
                method.invoke(t, params == null ? (Object) null : params);
                return true;
            }
        }
        if (classUsedForOverrides.getSuperclass() != null) {
            return _processCustom(setterOverrides, classUsedForOverrides.getSuperclass(), method, t);
        }
        return false;
    }

    protected Class[] _toClasses(Object[] objects) throws ClassNotFoundException {
        return _toClasses(new String[objects.length], objects);
    }

    protected Class[] _toClasses(String[] classNames, Object[] objects) throws ClassNotFoundException {
        Class[] classesAr = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            if (classNames[i] != null) {
                classesAr[i] = ClassUtils.forName(classNames[i], null);
            }
            else {
                classesAr[i] = objects[i].getClass();
            }
        }
        return classesAr;
    }

    protected Class<?> _toClass(Type type) {
        return TypeUtils.getRawType(type, type);
    }

    protected int _getParameterTypeCount(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments().length;
    }

    protected Type _getParameterType(ParameterizedType type) {
        return type.getActualTypeArguments()[0];
    }

    protected Type[] _getParameterTypes(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments();
    }

}

