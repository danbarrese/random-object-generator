package net.pladform.random;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Generates objects using reflection.
 */
public class ObjectGenerator extends BaseGenerator {

    public ObjectGenerator() {
    }

    public <T> T generate(Class<T> klass, Class<T>... constructorTypes) {
        try {
            T t = klass.getConstructor(constructorTypes).newInstance();
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

    public <T> T generate(Class<T> klass, Map<String, Callable> methodNameFunctions, Class<T>... constructorTypes) {
        try {
            T t = klass.getConstructor(constructorTypes).newInstance();
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
            Object params = methodNameFunctions.get(method.getName()).call();
            if (isBaseType(params)) {
                method.invoke(t, params);
            } else {
                method.invoke(t, generate(params.getClass()));
            }
            return true;
        }
        else {
            return false;
        }
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

}
