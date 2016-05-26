package net.pladform.random;

import net.pladform.Parent;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Dan Barrese
 */
public class ParentChildTest {

    private static ObjectGenerator g = new IdAwareObjectGenerator();

    @Test
    public void testInfiniteRecursion() throws Exception {
        Map<String, Callable> setterOverrides = new HashMap<>();
        setterOverrides.put("Child.setParent", () -> null);
        Parent o = g.generate(Parent.class, setterOverrides);
        System.out.println(o);
    }

}
