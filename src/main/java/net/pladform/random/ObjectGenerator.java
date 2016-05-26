package net.pladform.random;

import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates objects using reflection.
 */
public class ObjectGenerator extends BaseGenerator {

    public ObjectGenerator() {
    }

    public <T> T generate(Class<T> klass) {
        System.out.println(klass);
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
    
    // -----------------------
    // protected methods
    // -----------------------

    protected <T> void processMethod(Method method, Map<String, Callable> methodNameFunctions, T t) throws Exception {
        boolean done = processCustom(methodNameFunctions, method, t);
        if (!done) {
            processNormal(method, t);
        }
    }

    protected <T> boolean processNormal(Method method, T t) throws InvocationTargetException, IllegalAccessException {
        if (method.getName().startsWith("set") && method.getName().length() > 3) {
            Class[] types = method.getParameterTypes();
            Object[] params = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                if (isBaseType(types[i])) {
                    params[i] = random(types[i]);
                } else {
                    params[i] = generate(types[i]);
                }
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
            System.out.println(String.format("custom: %s.%s", t.getClass().getSimpleName(), method.getName()));
            Object params = methodNameFunctions.get(method.getName()).call();
            if (params == null) {
                method.invoke(t, (Object) null);
            //            } else if (isBaseType(params)) {
            //                method.invoke(t, params);
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

    //    protected <T> boolean processCustom(Map<String, Function> methodNameFunctions, Method method, T t) throws InvocationTargetException, IllegalAccessException {
//        if (methodNameFunctions != null && methodNameFunctions.containsKey(method.getName())) {
//            Object params = methodNameFunctions.get(method.getName()).apply(t);
//            if (isBaseType(params)) {
//                method.invoke(t, params);
//            } else {
//                method.invoke(t, generate(params.getClass()));
//            }
//            return true;
//        }
//        else {
//            return false;
//        }
//    }

//    protected <T> boolean processNormal(Method method, T t) throws InvocationTargetException, IllegalAccessException {
//        if (method.getName().startsWith("set") && method.getName().length() > 3) {
//            System.out.println(String.format("normal: %s.%s", t.getClass().getSimpleName(), method.getName()));
//
//            //            Stream.of(method.getGenericParameterTypes())
//            //                    .filter(type -> type instanceof ParameterizedType)
//            //                    .map(type -> ((ParameterizedType) type).getActualTypeArguments()[0])
//            //                    .forEach(type -> System.out.println(String.format("  -> %s", type)));
//            List parameterizedTypes = Stream.of(method.getGenericParameterTypes())
//                    .filter(type -> type instanceof ParameterizedType)
//                    .map(type -> ((ParameterizedType) type).getActualTypeArguments()[0])
//                    .collect(Collectors.toList());
//
//            List<Class> nonParameterizedTypes = Stream.of(method.getParameterTypes())
//                    .filter(type -> type.getTypeParameters() == null || type.getTypeParameters().length == 0)
//                    .collect(Collectors.toList());
//            Object[] params = new Object[nonParameterizedTypes.size() + parameterizedTypes.size()];
//            for (int i = 0; i < nonParameterizedTypes.size(); i++) {
//                if (isBaseType(nonParameterizedTypes.get(i))) {
//                    params[i] = random(nonParameterizedTypes.get(i));
//                }
//                else {
//                    params[i] = generate(nonParameterizedTypes.get(i));
//                }
//            }
//
//            for (int i = 0; i < parameterizedTypes.size(); i++) {
//                //                if (isBaseType(parameterizedTypes.get(i))) {
//                if (isBaseType(
//                        TypeUtils.getRawType((Type) parameterizedTypes.get(i), (Type) parameterizedTypes.get(i)))) {
//                    //                    params[i] = random(parameterizedTypes.get(i));
//                    params[i] = randomCollection(
//                            TypeUtils.getRawType((Type) parameterizedTypes.get(i), (Type) parameterizedTypes.get(i)),
//                            Set.class, randomInt(0, 10));
//                }
//                else {
//                    //                    params[i] = generate(parameterizedTypes.get(i));
//                }
//            }
//
//            if (params.length > 0) {
//                method.invoke(t, params);
//            }
//            return true;
//        }
//        else {
//            return false;
//        }
//    }

}
