package net.pladform.random;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dan Barrese
 */
public class BaseGeneratorTest {

    BaseGenerator g = new BaseGenerator();

    private void verifyRandomIntsInRange(int min, int max) {
        int r;
        for (int i = 0; i < 100000; i++) {
            r = g.randomInt(min, max);
            Assert.assertTrue(String.format("%d is not in range[%d, %d]", r, min, max), r >= min && r <= max);
        }
    }

    @Test
    public void randomInt() throws Exception {
        verifyRandomIntsInRange(0, 1);
        verifyRandomIntsInRange(0, 10);
        verifyRandomIntsInRange(0, 1000);
        verifyRandomIntsInRange(5, 6);
        verifyRandomIntsInRange(5, 10);
        verifyRandomIntsInRange(5, 1000);
        verifyRandomIntsInRange(-1, 0);
        verifyRandomIntsInRange(-10, 0);
        verifyRandomIntsInRange(-1000, 0);
        verifyRandomIntsInRange(-1000, -5);
        verifyRandomIntsInRange(-10, -9);
        verifyRandomIntsInRange(-10, -5);
        verifyRandomIntsInRange(-10, -1);
        verifyRandomIntsInRange(-1000, -999);
        verifyRandomIntsInRange(-1000, -5);
        verifyRandomIntsInRange(-1000, -1);
        verifyRandomIntsInRange(-10, 10);
        verifyRandomIntsInRange(-11, 10);
        verifyRandomIntsInRange(-10, 11);
        verifyRandomIntsInRange(-1000, 10);
        verifyRandomIntsInRange(-10, 1000);
        verifyRandomIntsInRange(0, Integer.MAX_VALUE - 10);
        verifyRandomIntsInRange(0, Integer.MAX_VALUE - 1);
        verifyRandomIntsInRange(Integer.MIN_VALUE, 0);
        verifyRandomIntsInRange(Integer.MIN_VALUE, 1);
        verifyRandomIntsInRange(Integer.MIN_VALUE, 10);
        verifyRandomIntsInRange(0, Integer.MAX_VALUE);
        verifyRandomIntsInRange(-1, Integer.MAX_VALUE);
        verifyRandomIntsInRange(-10, Integer.MAX_VALUE);
        verifyRandomIntsInRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void randomString() throws Exception {
        System.out.println(g.randomString());
    }

    @Test
    public void randomWords() throws Exception {
        System.out.println(g.randomWords(100));
    }

    @Test
    public void testNextId() throws Exception {
        Long l1 = g.nextId();
        Long l2 = g.nextId();
        Long l3 = g.nextId();
        Assert.assertEquals((Long) 1L, l1);
        Assert.assertEquals((Long) 2L, l2);
        Assert.assertEquals((Long) 3L, l3);
    }

    @Test
    public void testChooseOrCreateNew() throws Exception {
        Set<Long> ids  = new HashSet<>();
        ids.add(0L);
        for (int i = 0; i < 100; i++) {
            Long l = g.chooseOrCreateNew(ids, 0.2, () -> g.nextId());
            System.out.println(l);
        }
    }

}
