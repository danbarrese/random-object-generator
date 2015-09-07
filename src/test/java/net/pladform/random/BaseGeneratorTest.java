package net.pladform.random;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class BaseGeneratorTest {

    Random r = new Random();
    BaseGenerator baseGenerator = new BaseGenerator();

    private void verifyRandomIntsInRange(int min, int max) {
        int r;
        for (int i = 0; i < 100000; i++) {
            r = baseGenerator.randomInt(min, max);
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

//        for (int i = 0; i < 100; i++) {
//            System.out.println(baseGenerator.randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
//        }
    }

    @Test
    public void randomString() throws Exception {
        System.out.println(baseGenerator.randomString());
    }

    @Test
    public void words() throws Exception {
        System.out.println(baseGenerator.words(5));
    }

}
