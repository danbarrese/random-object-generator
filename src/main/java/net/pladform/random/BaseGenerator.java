package net.pladform.random;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Responsible for generation of random Strings, Longs, ints, etc.
 */
public class BaseGenerator {

    private Random random;
    private String characterSet = " \tabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890`~!@#$%^&*()_+[]\\{}|;':\",.<>/?";
    private AtomicLong id;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public BaseGenerator() {
        random = new Random();
        id = new AtomicLong(1);
    }

    public <T> T random(Class<T> type) {
        if (type.equals(String.class)) {
            return (T) randomString();
        } else if (type.equals(Integer.class)) {
            return (T) randomInt();
        } else if (type.equals(Long.class)) {
            return (T) randomLong();
        } else if (type.equals(Double.class)) {
            return (T) randomDouble();
        } else if (type.equals(Date.class)) {
            return (T) randomDate();
        } else {
            throw new IllegalArgumentException("Don't know how to generate a random " + type.getName());
        }
    }

    public <T> boolean isBaseType(Class<T> type) {
        if (type.equals(String.class)) {
            return true;
        } else if (type.equals(Integer.class)) {
            return true;
        } else if (type.equals(Long.class)) {
            return true;
        } else if (type.equals(Double.class)) {
            return true;
        } else if (type.equals(Date.class)) {
            return true;
        } else {
            return false;
        }
    }

    public <T> boolean isBaseType(Object o) {
        return o instanceof String || o instanceof Integer || o instanceof Long || o instanceof Double || o instanceof Date;
    }

    public String randomString() {
        int len = randomInt(0, 100);
        if (len == 0) {
            return "";
        }
        char[] str = new char[len];
        for (int i = 0; i < len; i++) {
            str[i] = randomChar();
        }
        return new String(str);
    }

    public Character randomChar() {
        return characterSet.charAt(randomInt(0, characterSet.length() - 1));
    }

    public Integer randomInt() {
        return randomInt(0, 10000);
    }

    public Integer randomInt(int lowerBound, int upperBound) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("upperBound cannot be less than lowerBound.");
        }
        int i = upperBound - lowerBound + 1;
        if (upperBound >= 0 && i <= 0) {
            long l = randomLong(lowerBound, upperBound);
            return (int) l;
        }
        return random.nextInt(i) + lowerBound;
    }

    public Long randomLong() {
        return randomLong(-1000000L, 1000000L);
    }

    public Long randomLong(long lowerBound, long upperBound) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("upperBound cannot be less than lowerBound.");
        }
        long l = upperBound - lowerBound + 1;
        if (upperBound >= 0 && l < 0) {
            throw new IllegalStateException("numeric overflow");
        }
        return nextLong(l) + lowerBound;
    }

    public Double randomDouble() {
        return randomDouble(0.0, 1000000.0);
    }

    public Double randomDouble(double lowerBound, double upperBound) {
        return lowerBound + (upperBound - lowerBound) * random.nextDouble();
    }

    public Long nextId() {
        return id.getAndIncrement();
    }

    public Date randomDate(String fromDate, String toDate) {
        long lowerBound = dateTimeFormatter.parseDateTime(fromDate).getMillis();
        DateTime upperBoundDateTime = dateTimeFormatter.parseDateTime(toDate);
        long upperBound = upperBoundDateTime.getMillis();
        long range = upperBound - lowerBound + 1;
        return upperBoundDateTime.minus(randomLong(0, range)).toDate();
    }

    public Date randomDate(String fromDate) {
        DateTime now = new DateTime(new Date());
        return randomDate(fromDate, now.toString(dateTimeFormatter));
    }

    public Date randomDate() {
        DateTime epoch = new DateTime(0L);
        DateTime now = new DateTime(new Date());
        return randomDate(epoch.toString(dateTimeFormatter), now.toString(dateTimeFormatter));
    }

    public <T> T choose(T[] elements) {
        if (elements == null || elements.length == 0) {
            return null;
        }
        return elements[randomInt(0, elements.length - 1)];
    }

    public <T> T choose(List<T> elements) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        return elements.get(randomInt(0, elements.size() - 1));
    }

    public <T> T choose(Set<T> elements) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        int idx = randomInt(0, elements.size() - 1);
        Iterator<T> iter = elements.iterator();
        for (int i = 0; i < idx - 1; i++) {
            iter.next();
        }
        return iter.next();
    }

    // private methods
    // ------------------------------------

    private Long nextLong(long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }

}
