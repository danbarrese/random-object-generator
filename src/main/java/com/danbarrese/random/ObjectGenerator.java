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
package com.danbarrese.random;

import com.danbarrese.random.config.GeneratorConfig;
import com.danbarrese.random.exception.FailedRandomObjectGenerationException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.util.ClassUtils;

/**
 * Generates objects using reflection.
 */
@SuppressWarnings({"unchecked"})
public class ObjectGenerator extends BaseGenerator {

    public ObjectGenerator() {
        super();
    }

    public ObjectGenerator(GeneratorConfig config) {
        super(config);
    }

    public <T> T generate(Class<T> klass) {
        return generate(klass, new ArrayDeque<>(), null, null);
    }

    @SuppressWarnings("unused")
    public <T> T generate(Class<T> klass, Object[] constructorArgs) {
        Validate.notNull(klass);
        Validate.notNull(constructorArgs);
        try {
            Class<?>[] constructorTypes = _toClasses(constructorArgs);
            return generate(klass, new ArrayDeque<>(), constructorTypes, constructorArgs);
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    public <T> T generate(Class<T> klass, Class<?>[] constructorTypes, Object[] constructorArgs) {
        Validate.notNull(klass);
        Validate.notNull(constructorArgs);
        return generate(klass, new ArrayDeque<>(), constructorTypes, constructorArgs);
    }

    // -----------------------
    // protected methods
    // -----------------------

    protected <T> T generate(
            Class<T> klass,
            Deque<Object> objectStack,
            Class<?>[] constructorTypes,
            Object[] constructorArgs
    ) {
        Validate.notNull(klass);
        if (constructorTypes == null) {
            constructorTypes = new Class<?>[0];
        }
        if (constructorArgs == null) {
            constructorArgs = new Object[0];
        }
        if (config.verbose) {
            log(String.format("generating object of type: %s, with args: %s, with overrides: %s", klass,
                    Arrays.toString(constructorArgs),
                    config.fieldOverrides));
        }
        try {
            if (klass.isEnum()) {
                int randomOrdinal = randomInt(0, klass.getEnumConstants().length - 1);
                return klass.getEnumConstants()[randomOrdinal];
            } else {
                T t = constructNew(klass, constructorTypes, constructorArgs);
                objectStack.push(t);
                Class<?> classOrSuperclass = klass;
                while (classOrSuperclass != null) {
                    Field[] fields = classOrSuperclass.getDeclaredFields();
                    for (Field field : fields) {
                        _processField(field, t, objectStack);
                    }
                    classOrSuperclass = classOrSuperclass.getSuperclass();
                }
                objectStack.pop();
                return t;
            }
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    protected <T, E> Collection<T> randomCollection(Type elementType,
                                                    Class<E> collectionType,
                                                    int count,
                                                    Deque<Object> objectStack) {
        Validate.isTrue(count >= 0);
        Validate.notNull(elementType);
        Validate.notNull(collectionType);
        if (config.verbose) {
            log(String.format("generating %s<%s> (%d)", collectionType, elementType, count));
        }
        Collection<T> collection;
        if (collectionType.equals(List.class)) {
            collection = new ArrayList<>();
        } else if (collectionType.equals(Set.class)) {
            collection = new HashSet<>();
        } else if (collectionType.equals(Deque.class)) {
            collection = new ArrayDeque<>();
        } else if (collectionType.equals(Collection.class)) {
            collection = new ArrayList<>();
        } else {
            throw new IllegalArgumentException(
                    "Don't know how to generate a random collection of type: " + collectionType.getName());
        }
        for (int i = 0; i < count; i++) {
            collection.add((T) _process(elementType, objectStack));
        }
        return collection;
    }

    protected <T, S, E> Map<T, S> randomMap(Type[] elementTypes,
                                            Class<E> collectionType,
                                            int count,
                                            Deque<Object> objectStack) {
        Validate.isTrue(count >= 0);
        Validate.notNull(elementTypes);
        Validate.notNull(collectionType);
        Map<T, S> collection;
        if (collectionType.equals(Map.class)) {
            collection = new HashMap<>();
        } else {
            throw new IllegalArgumentException(
                    "Don't know how to generate a random multi-param collection of type: " + collectionType.getName());
        }
        for (int i = 0; i < count; i++) {
            collection.put((T) _process(elementTypes[0], objectStack),
                    (S) _process(elementTypes[1], objectStack));
        }
        return collection;
    }

    protected <T> void _processField(Field field, T t, Deque<Object> objectStack) throws Exception {
        boolean done = _processCustom(field, t);
        if (!done) {
            _processNormal(field, t, objectStack);
        }
    }

    protected Object _process(Type type) {
        return _process(type, new ArrayDeque<>());
    }

    protected Object _process(Type type, Deque<Object> objectStack) {
        Class<?> klass = _toClass(type);
        if (type instanceof ParameterizedType) {
            if (_getParameterTypeCount(type) == 1) {
                Type parameterType = _getParameterType((ParameterizedType) type);
                return randomCollection(parameterType,
                        klass,
                        randomInt(config.DEFAULT_SET_SIZE_MIN, config.DEFAULT_SET_SIZE_MAX),
                        objectStack);
            } else {
                // assume we want a Map... for now..
                // TODO: what else could we support here?  Say MyObject<One, Two>?
                Type[] parameterTypes = _getParameterTypes(type);
                return randomMap(parameterTypes,
                        klass,
                        randomInt(config.DEFAULT_SET_SIZE_MIN, config.DEFAULT_SET_SIZE_MAX),
                        objectStack);
            }
        }
        if (isBaseType(klass)) {
            return random(klass);
        }
        if (_getObjectByType(objectStack, type) != null) {
            return _getObjectByType(objectStack, type);
        } else {
            return generate(klass, objectStack, null, null);
        }
    }

    protected Object _getObjectByType(Deque<Object> objectStack, Type type) {
        Class<?> klass = _toClass(type);
        for (Object o : objectStack) {
            if (o.getClass().equals(klass)) {
                return o;
            }
        }
        return null;
    }

    protected <T> void _processNormal(
            Field field,
            T t,
            Deque<Object> objectStack
    ) throws IllegalAccessException {
        field.setAccessible(true);
        Type type = field.getType();
        Object param = _process(type, objectStack);
        field.set(t, param);
    }

    @SuppressWarnings("RedundantIfStatement")
    protected <T> boolean _processCustom(Field field, T t)
            throws Exception {
        if (config.fieldOverrides != null) {
            Class<?> klass = t.getClass();
            if (_processCustom(klass, field, t)) {
                return true;
            }
        }
        return false;
    }

    protected <T> boolean _processCustom(Class<?> classUsedForOverrides,
                                         Field field,
                                         T t) throws Exception {
        field.setAccessible(true);
        if (config.fieldOverrides != null) {
            Callable<?> c = config.fieldOverrides.get(classUsedForOverrides, field.getName());
            if (c != null) {
                Object params = c.call();
                field.set(t, params);
                return true;
            }
        }

        if (config.fieldOverrides != null) {
            Callable<?> c = config.fieldOverrides.get(classUsedForOverrides, field.getName());
            if (c != null) {
                Object params = c.call();
                field.set(t, params);
                return true;
            }
        }
        if (classUsedForOverrides.getSuperclass() != null) {
            return _processCustom(classUsedForOverrides.getSuperclass(), field, t);
        }
        return false;
    }

    protected Class<?>[] _toClasses(Object[] objects) throws ClassNotFoundException {
        return _toClasses(new String[objects.length], objects);
    }

    protected Class<?>[] _toClasses(String[] classNames, Object[] objects) throws ClassNotFoundException {
        Class<?>[] classesAr = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            if (classNames[i] != null) {
                classesAr[i] = ClassUtils.forName(classNames[i], null);
            } else {
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

    protected <T> T constructNew(Class<?> klass, Class<?>[] constructorTypes, Object[] constructorArgs) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (HashSet.class.isAssignableFrom(klass)) {
            return (T) new HashSet<>();
        } else if (TreeSet.class.isAssignableFrom(klass)) {
            return (T) new TreeSet<>();
        } else if (Set.class.isAssignableFrom(klass)) {
            return (T) new HashSet<>();
        } else if (ArrayList.class.isAssignableFrom(klass)) {
            return (T) new ArrayList<>();
        } else if (LinkedList.class.isAssignableFrom(klass)) {
            return (T) new LinkedList<>();
        } else if (List.class.isAssignableFrom(klass)) {
            return (T) new ArrayList<>();
        } else if (HashMap.class.isAssignableFrom(klass)) {
            return (T) new HashMap<>();
        } else if (TreeMap.class.isAssignableFrom(klass)) {
            return (T) new TreeMap<>();
        } else if (Map.class.isAssignableFrom(klass)) {
            return (T) new HashMap<>();
        } else if (Deque.class.isAssignableFrom(klass)) {
            return (T) new ArrayDeque<>();
        } else {
            return (T) klass.getConstructor(constructorTypes).newInstance(constructorArgs);
        }
    }

}

