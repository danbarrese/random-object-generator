package net.pladform.random;

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
public class ObjectGenerator extends BaseGenerator {

    public int DEFAULT_SET_SIZE_MIN = 0;
    public int DEFAULT_SET_SIZE_MAX = 10;

    public ObjectGenerator() {
    }

    public <T> T generate(Class<T> klass) {
        try {
            T t = klass.getConstructor().newInstance();
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                processMethod(method, null, t);
            }
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FailedRandomObjectGenerationException(e);
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }
    
    public <T> T generate(Class<T> klass, Object... constructorArgs) {
        try {
            Class[] constructorTypes = toClasses(constructorArgs);
            T t = klass.getConstructor(constructorTypes).newInstance(constructorArgs);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                processMethod(method, null, t);
            }
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FailedRandomObjectGenerationException(e);
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    public <T> T generate(Class<T> klass, Map<String, Callable> methodNameFunctions, Object... constructorArgs) {
        try {
            Class[] constructorTypes = toClasses(constructorArgs);
            T t = klass.getConstructor(constructorTypes).newInstance(constructorArgs);
            Method[] methods = klass.getMethods();
            for (Method method : methods) {
                processMethod(method, methodNameFunctions, t);
            }
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FailedRandomObjectGenerationException(e);
        } catch (Exception e) {
            throw new FailedRandomObjectGenerationException(e);
        }
    }

    public <T, E> Collection<T> randomCollection(Type elementType, Class<E> collectionType, int count) {
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
            collection.add((T) process(elementType));
        }
        return collection;
    }

    // -----------------------
    // protected methods
    // -----------------------

    protected <T> void processMethod(Method method, Map<String, Callable> methodNameFunctions, T t) throws Exception {
        boolean done = processCustom(methodNameFunctions, method, t);
        if (!done) {
            processNormal(method, t);
        }
    }

    protected Object process(Type type) {
        Class<?> clazz = toClass(type);
        if (type instanceof ParameterizedType) {
            Type parameterType = getParameterType((ParameterizedType) type);
            return randomCollection(parameterType, clazz, randomInt(DEFAULT_SET_SIZE_MIN, DEFAULT_SET_SIZE_MAX));
        }
        if (isBaseType(clazz)) {
            return random(clazz);
        } else {
            return generate(clazz);
        }
    }

    protected <T> boolean processNormal(Method method, T t) throws InvocationTargetException, IllegalAccessException {
        if (method.getName().startsWith("set") && method.getName().length() > 3) {
            Type[] types = method.getGenericParameterTypes();

            Object[] params = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                params[i] = process(types[i]);
            }
            method.invoke(t, params);
            return true;
        }
        else {
            return false;
        }
    }

    protected <T> boolean processCustom(Map<String, Callable> methodNameFunctions, Method method, T t) throws Exception {
        if (methodNameFunctions != null && methodNameFunctions.containsKey(method.getName())) {
            Object params = methodNameFunctions.get(method.getName()).call();
            if (params == null) {
                method.invoke(t, (Object) null);
            } else {
                method.invoke(t, params);
            }
            return true;
        }
        else {
            return false;
        }
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
