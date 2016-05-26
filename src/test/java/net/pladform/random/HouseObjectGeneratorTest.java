package net.pladform.random;

import net.pladform.House;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;

public class HouseObjectGeneratorTest {

    private static ObjectGenerator g = new IdAwareObjectGenerator();
    private static Set<String> roomNames = new HashSet<>();
    private static Set<Integer> widths = new HashSet<>();
    private static Set<Integer> heights = new HashSet<>();
    private static Set<Integer> depths = new HashSet<>();

    @BeforeClass
    public static void beforeClass() throws Exception {
        g.CHARACTER_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    }

    @Test
    public void testGenerateHouse() throws Exception {
        Map<String, Callable> setterOverrides = new HashMap<>();
        setterOverrides.put("setName", () -> "blahhh");
        House o = g.generate(House.class, setterOverrides);
        System.out.println(o);
    }

}
