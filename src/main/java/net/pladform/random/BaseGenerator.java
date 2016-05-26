package net.pladform.random;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Responsible for generation of random Strings, Longs, ints, etc.
 * @author Dan Barrese
 */
@SuppressWarnings({"unchecked", "unused", "FieldCanBeLocal"})
public class BaseGenerator {

    public String CHARACTER_SET = " \tabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890`~!@#$%^&*()_+[]\\{}|;':\",.<>/?";
    public int DEFAULT_STRING_LENGTH_MIN = 1;
    public int DEFAULT_STRING_LENGTH_MAX = 25;
    public int DEFAULT_INT_MIN = 0;
    public int DEFAULT_INT_MAX = 1000;
    public long DEFAULT_LONG_MIN = -1000000L;
    public long DEFAULT_LONG_MAX = 1000000L;
    public double DEFAULT_DOUBLE_MIN = 0.0;
    public double DEFAULT_DOUBLE_MAX = 1000000.0;
    public String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Random random;
    private Map<String, AtomicLong> idGenerator;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT);
    private List<String> dictionary;

    // ------------------------
    // constructors
    // ------------------------

    public BaseGenerator() {
        random = new Random();
        idGenerator = new HashMap<>();
    }

    // ------------------------
    // public methods
    // ------------------------

    public <T> T random(Class<T> type) {
        if (type.equals(String.class)) {
            return (T) randomString();
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return (T) randomInt();
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return (T) randomLong();
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return (T) randomDouble();
        } else if (type.equals(Date.class)) {
            return (T) randomDate();
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (T) randomBoolean();
        } else if (type.equals(Character.class) || type.equals(char.class)) {
            return (T) randomChar();
        } else {
            throw new IllegalArgumentException("Don't know how to generate a random " + type.getName());
        }
    }

    public <T> boolean isBaseType(Class<T> type) {
        if (type.equals(String.class)) {
            return true;
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return true;
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return true;
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return true;
        } else if (type.equals(Date.class)) {
            return true;
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return true;
        } else if (type.equals(Character.class) || type.equals(char.class)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBaseType(Object o) {
        return o instanceof String || o instanceof Integer || o instanceof Long || o instanceof Double || o instanceof Date;
    }

    public String randomString() {
        int len = randomInt(DEFAULT_STRING_LENGTH_MIN, DEFAULT_STRING_LENGTH_MAX);
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
        return CHARACTER_SET.charAt(randomInt(0, CHARACTER_SET.length() - 1));
    }

    public Integer randomInt() {
        return randomInt(DEFAULT_INT_MIN, DEFAULT_INT_MAX);
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
        return randomLong(DEFAULT_LONG_MIN, DEFAULT_LONG_MAX);
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
        return randomDouble(DEFAULT_DOUBLE_MIN, DEFAULT_DOUBLE_MAX);
    }

    public Double randomDouble(double lowerBound, double upperBound) {
        return lowerBound + (upperBound - lowerBound) * random.nextDouble();
    }

    public Long nextId() {
        return nextId("default");
    }

    public Long nextId(String arbitraryGeneratorName) {
        return idGenerator.getOrDefault(arbitraryGeneratorName, new AtomicLong(1)).getAndIncrement();
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

    public Boolean randomBoolean() {
        return randomInt(0, 1) == 1;
    }

    public <T> T choose(T... elements) {
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

    public <T> Set<T> choose(Set<T> elements, int count) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        if (count > elements.size()) {
            count = elements.size();
        }
        Set<Integer> chosenIndexes = new HashSet<>();
        while (chosenIndexes.size() != count) {
            chosenIndexes.add(randomInt(0, elements.size() - 1));
        }
        Iterator<T> iter = elements.iterator();
        Set<T> chosenElements = new HashSet<>();
        for (int i = 0; i < elements.size(); i++) {
            T t = iter.next();
            if (chosenIndexes.contains(i)) {
                chosenElements.add(t);
                if (chosenElements.size() == count) {
                    break;
                }
            }
        }
        return chosenElements;
    }

    public String words(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("word count must be > 0.");
        }
        if (dictionary == null) {
            loadDictionary();
        }
        StringBuilder words = new StringBuilder();
        words.append(choose(dictionary));
        for (int i = 1; i < count; i++) {
            words.append(" ");
            words.append(choose(dictionary));
        }
        return words.toString();
    }

    // ------------------------
    // private methods
    // ------------------------

    private Long nextLong(long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }

    private void loadDictionary() {
        dictionary = new ArrayList<>(60400);
        BufferedReader reader = null;
        try {
            InputStream fis = RootObject.class.getResourceAsStream("dict.txt");
            reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                dictionary.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            // TODO: handle exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
