package net.pladform.random;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Generates objects using reflection.
 */
public class IdAwareObjectGenerator extends ObjectGenerator {

    public IdAwareObjectGenerator() {
        super();
    }

    protected <T> void processMethod(Method method, Map<String, Callable> setterOverrides, T t) throws Exception {
        boolean done = processCustom(setterOverrides, method, t);
        if (!done && (method.getName().endsWith("Id")) || method.getName().endsWith("ID")) {
            if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(Long.class)) {
                method.invoke(t, nextId());
                done = true;
            }
        }
        if (!done) {
            processNormal(method, setterOverrides, t);
        }
    }

}
