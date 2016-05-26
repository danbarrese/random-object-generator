package net.pladform.random;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates objects using reflection.
 */
@SuppressWarnings({"unchecked"})
public class ObjectGenerator extends BaseGenerator {

    public int DEFAULT_SET_SIZE_MIN = 0;
    public int DEFAULT_SET_SIZE_MAX = 10;

    public ObjectGenerator() {
    }

    public <T> T generate(Class<T> klass) {
        Validate.notNull(klass);
        try {
            T t = klass.getConstructor().newInstance();
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                processMethod(method, null, t);
            }
            return t;
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    @SuppressWarnings("unused")
    public <T> T generate(Class<T> klass, Object... constructorArgs) {
        Validate.notNull(klass);
        Validate.notNull(constructorArgs);
        try {
            Class[] constructorTypes = toClasses(constructorArgs);
            T t = klass.getConstructor(constructorTypes).newInstance(constructorArgs);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                processMethod(method, null, t);
            }
            return t;
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    public <T> T generate(Class<T> klass, Map<String, Callable> setterOverrides, Object... constructorArgs) {
        Validate.notNull(klass);
        Validate.notNull(constructorArgs);
        try {
            Class[] constructorTypes = toClasses(constructorArgs);
            T t = klass.getConstructor(constructorTypes).newInstance(constructorArgs);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                processMethod(method, setterOverrides, t);
            }
            return t;
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    public <T, E> Collection<T> randomCollection(Type elementType,
                                                 Class<E> collectionType,
                                                 Map<String, Callable> setterOverrides,
                                                 int count) {
        Validate.notNull(elementType);
        Validate.notNull(collectionType);
        Collection<T> collection;
        if (collectionType.equals(List.class)) {
            collection = new ArrayList<>();
        } else if (collectionType.equals(Set.class)) {
            collection = new HashSet<>();
        } else if (collectionType.equals(Deque.class)) {
            collection = new ArrayDeque<>();
        } else {
            throw new IllegalArgumentException("Don't know how to generate a random collection of type: " + collectionType.getName());
        }
        for (int i = 0; i < count; i++) {
            collection.add((T) process(elementType, setterOverrides));
        }
        return collection;
    }

    // -----------------------
    // protected methods
    // -----------------------

    protected <T> void processMethod(Method method, Map<String, Callable> setterOverrides, T t) throws Exception {
        boolean done = processCustom(setterOverrides, method, t);
        if (!done) {
            processNormal(method, setterOverrides, t);
        }
    }

    protected Object process(Type type, Map<String, Callable> setterOverrides) {
        Class<?> klass = toClass(type);
        if (type instanceof ParameterizedType) {
            Type parameterType = getParameterType((ParameterizedType) type);
            return randomCollection(parameterType,
                    klass,
                    setterOverrides,
                    randomInt(DEFAULT_SET_SIZE_MIN, DEFAULT_SET_SIZE_MAX));
        }
        if (isBaseType(klass)) {
            return random(klass);
        } else {
            return generate(klass, setterOverrides);
        }
    }

    protected <T> boolean processNormal(Method method, Map<String, Callable> setterOverrides, T t) throws InvocationTargetException, IllegalAccessException {
        if (method.getName().startsWith("set") && method.getName().length() > 3) {
            Type[] types = method.getGenericParameterTypes();

            Object[] params = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                params[i] = process(types[i], setterOverrides);
            }
            method.invoke(t, params);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("RedundantCast")
    protected <T> boolean processCustom(Map<String, Callable> setterOverrides,
                                        Method method,
                                        T t) throws Exception {
        if (setterOverrides != null) {
            String classAndMethod = String.format("%s.%s",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName());
            if (setterOverrides.containsKey(classAndMethod)) {
                Object params = setterOverrides.get(classAndMethod).call();
                method.invoke(t, params == null ? (Object) null : params);
                return true;
            } else if (setterOverrides.containsKey(method.getName())) {
                Object params = setterOverrides.get(method.getName()).call();
                method.invoke(t, params == null ? (Object) null : params);
                return true;
            }
        }
        return false;
    }

    protected Class[] toClasses(Object[] objects) {
        List<Class<?>> classList = Stream.of(objects)
                .map(Object::getClass)
                .collect(Collectors.toList());
        Class[] classesAr = new Class[classList.size()];
        for (int i = 0; i < classList.size(); i++) {
            classesAr[i] = classList.get(i);
        }
        return classesAr;
    }

    protected Class<?> toClass(Type type) {
        return TypeUtils.getRawType(type, type);
    }

    protected Type getParameterType(ParameterizedType type) {
        return type.getActualTypeArguments()[0];
    }

}

