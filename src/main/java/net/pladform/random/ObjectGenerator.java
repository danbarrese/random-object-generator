package net.pladform.random;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

/**
 * Generates objects using reflection.
 */
public class ObjectGenerator extends BaseGenerator {

    public ObjectGenerator() {
    }

    public <T> T generate(Class<T> klass, Class<T>... constructorTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T t = klass.getConstructor(constructorTypes).newInstance();
        Method[] methods = klass.getMethods();
        for (Method method : methods) {
            processMethod(method, null, t);
        }
        return t;
    }

    public <T> T generate(Class<T> klass, Map<String, Function> methodNameFunctions, Class<T>... constructorTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T t = klass.getConstructor(constructorTypes).newInstance();
        Method[] methods = klass.getMethods();
        for (Method method : methods) {
            processMethod(method, methodNameFunctions, t);
        }
        return t;
    }

    protected <T> void processMethod(Method method, Map<String, Function> methodNameFunctions, T t) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        boolean done = processCustom(methodNameFunctions, method, t);
        if (!done) {
            processNormal(method, t);
        }
    }

    protected <T> boolean processNormal(Method method, T t) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
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

    protected <T> boolean processCustom(Map<String, Function> methodNameFunctions, Method method, T t) throws InvocationTargetException, IllegalAccessException {
        if (methodNameFunctions != null && methodNameFunctions.containsKey(method.getName())) {
            Object[] params = (Object[]) methodNameFunctions.get(method.getName()).apply(t);
            method.invoke(t, params);
            return true;
        }
        else {
            return false;
        }
    }

}
