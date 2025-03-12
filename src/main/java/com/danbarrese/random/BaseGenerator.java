/*
 * Copyright 2016 Dan Barrese
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.danbarrese.random;

import com.danbarrese.random.config.GeneratorConfig;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Responsible for generation of random Strings, Longs, ints, etc.
 */
@SuppressWarnings({"unchecked", "unused", "FieldCanBeLocal"})
public class BaseGenerator {

    protected final GeneratorConfig config;
    private final Random random;
    private final Map<String, AtomicLong> idGenerator;
    private final DateTimeFormatter dateTimeFormatter;
    private Set<String> dictionary;

    // ------------------------
    // constructors
    // ------------------------

    public BaseGenerator() {
        this(new GeneratorConfig());
    }

    public BaseGenerator(GeneratorConfig config) {
        this.config = config;
        random = new Random(System.currentTimeMillis());
        idGenerator = new HashMap<>();
        dateTimeFormatter = DateTimeFormat.forPattern(config.DEFAULT_DATE_FORMAT);
    }

    // ------------------------
    // public methods
    // ------------------------

    public <T> T random(Class<T> type) {
        Validate.notNull(type);
        if (type.equals(String.class)) {
            return (T) randomWords(1);
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
        } else if (type.equals(BigDecimal.class)) {
            return (T) randomBigDecimal();
        } else if (type.equals(Serializable.class)) {
            return (T) nextId(type);
        } else {
            throw new IllegalArgumentException("Don't know how to generate a random " + type.getName());
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public <T> boolean isBaseType(Class<T> type) {
        Validate.notNull(type);
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
        } else if (type.equals(BigDecimal.class)) {
            return true;
        } else if (type.equals(Serializable.class)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBaseType(Object o) {
        Validate.notNull(o);
        return isBaseType(o.getClass());
    }

    public String randomString() {
        int len = randomInt(config.DEFAULT_STRING_LENGTH_MIN, config.DEFAULT_STRING_LENGTH_MAX);
        return randomString(len);
    }

    public String randomString(int len) {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_STRING)) {
            return null;
        }
        if (len == 0) {
            return "";
        }
        char[] str = new char[len];
        for (int i = 0; i < len; i++) {
            str[i] = randomChar();
        }
        return new String(str);
    }

    public Set<String> randomEmails(int count) {
        Set<String> emails = new HashSet<>();
        for (int i = 0; i < count; i++) {
            emails.add(randomEmail());
        }
        return emails;
    }

    public String randomEmail() {
        return randomEmailChars(20).replaceAll(" ", "") + "@" + randomEmailChars(20).replaceAll(" ", "")
                + "." + randomEmailChars(3).replaceAll(" ", "");
    }

    public String randomEmailChars(int len) {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_STRING)) {
            return null;
        }
        if (len == 0) {
            return "";
        }
        char[] str = new char[len];
        for (int i = 0; i < len; i++) {
            str[i] = randomEmailChar();
        }
        return new String(str);
    }

    public Character randomEmailChar() {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_CHAR)) {
            return null;
        }
        return config.EMAIL_CHAR_LIST.charAt(randomInt(0, config.EMAIL_CHAR_LIST.length() - 1));
    }

    public Character randomChar() {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_CHAR)) {
            return null;
        }
        return config.CHARACTER_SET.charAt(randomInt(0, config.CHARACTER_SET.length() - 1));
    }

    public Integer randomInt() {
        return randomInt(config.DEFAULT_INT_MIN, config.DEFAULT_INT_MAX);
    }

    public Integer randomInt(int lowerBound, int upperBound) {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_INT)) {
            return null;
        }
        Validate.isTrue(upperBound >= lowerBound);
        int i = upperBound - lowerBound + 1;
        if (upperBound >= 0 && i <= 0) {
            long l = randomLong(lowerBound, upperBound);
            return (int) l;
        }
        return random.nextInt(i) + lowerBound;
    }

    public Set<Integer> randomIntsDistinct(int lowerBound, int upperBound, int count) {
        Validate.isTrue(count >= 0);
        Validate.isTrue(count <= upperBound - lowerBound + 1);
        Set<Integer> ints = new HashSet<>();
        while (ints.size() != count) {
            ints.add(randomInt(lowerBound, upperBound));
        }
        return ints;
    }

    public Long randomLong() {
        return randomLong(config.DEFAULT_LONG_MIN, config.DEFAULT_LONG_MAX);
    }

    public Long randomLong(long lowerBound, long upperBound) {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_LONG)) {
            return null;
        }
        Validate.isTrue(upperBound >= lowerBound);
        long l = upperBound - lowerBound + 1;
        if (upperBound >= 0 && l < 0) {
            throw new IllegalStateException("numeric overflow");
        }
        return _nextLong(l) + lowerBound;
    }

    public Set<Long> randomLongsDistinct(long lowerBound, long upperBound, long count) {
        Validate.isTrue(count >= 0);
        Validate.isTrue(count <= upperBound - lowerBound);
        Set<Long> longs = new HashSet<>();
        while (longs.size() != count) {
            longs.add(randomLong(lowerBound, upperBound));
        }
        return longs;
    }

    public Double randomDouble() {
        return randomDouble(config.DEFAULT_DOUBLE_MIN, config.DEFAULT_DOUBLE_MAX);
    }

    public BigDecimal randomBigDecimal() {
        return BigDecimal.valueOf(randomDouble(config.DEFAULT_DOUBLE_MIN, config.DEFAULT_DOUBLE_MAX));
    }

    public Double randomProbability() {
        return random.nextDouble();
    }

    public Double randomDouble(double lowerBound, double upperBound) {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_DOUBLE)) {
            return null;
        }
        Validate.isTrue(upperBound >= lowerBound);
        return lowerBound + (upperBound - lowerBound) * random.nextDouble();
    }

    public Long nextId() {
        return nextId("default");
    }

    public Long nextId(String arbitraryGeneratorName) {
        Validate.notNull(arbitraryGeneratorName);
        return idGenerator.computeIfAbsent(arbitraryGeneratorName, s -> new AtomicLong(1)).getAndIncrement();
    }

    public Long nextId(Class<?> type) {
        Validate.notNull(type);
        return nextId(type.getName());
    }

    public Date randomDate(String fromDate, String toDate) {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_DATE)) {
            return null;
        }
        Validate.notNull(fromDate);
        Validate.notNull(toDate);
        long lowerBound = dateTimeFormatter.parseDateTime(fromDate).getMillis();
        DateTime upperBoundDateTime = dateTimeFormatter.parseDateTime(toDate);
        long upperBound = upperBoundDateTime.getMillis();
        long range = upperBound - lowerBound + 1;
        return upperBoundDateTime.minus(randomLong(0, range)).toDate();
    }

    public Date randomDate(String fromDate) {
        Validate.notNull(fromDate);
        DateTime now = new DateTime(new Date());
        return randomDate(fromDate, now.toString(dateTimeFormatter));
    }

    public Date randomDate() {
        DateTime epoch = new DateTime(0L);
        DateTime now = new DateTime(new Date());
        return randomDate(epoch.toString(dateTimeFormatter), now.toString(dateTimeFormatter));
    }

    public Date randomDateInLastNDays(int n) {
        Date temp = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(temp);
        c.add(Calendar.DAY_OF_YEAR, randomInt(-1 * n, -1));
        Date startDate = c.getTime();

        String startDateString = config.sdf.format(startDate);
        try {
            return config.sdf.parse(startDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Internal error formatting date during JUnit test.");
        }
    }

    public Boolean randomBoolean() {
        if (tryOdds(config.DEFAULT_CHANCE_OF_NULL_BOOLEAN)) {
            return null;
        }
        return randomInt(0, 1) == 1;
    }

    public <T> T choose(T... elements) {
        Validate.notNull(elements);
        Validate.isTrue(elements.length > 0);
        return elements[randomInt(0, elements.length - 1)];
    }

    public <T> T choose(Collection<T> elements) {
        Validate.notNull(elements);
        Validate.isTrue(elements.size() > 0);
        if (elements instanceof List) {
            return choose((List<T>) elements);
        } else if (elements instanceof Set) {
            return choose((Set<T>) elements);
        } else {
            throw new IllegalArgumentException(
                    "Don't know how to choose from collection of type: " + elements.getClass());
        }
    }

    public <T> T choose(List<T> elements) {
        Validate.notNull(elements);
        Validate.isTrue(elements.size() > 0);
        return elements.get(randomInt(0, elements.size() - 1));
    }

    public <T> T choose(Set<T> elements) {
        Validate.notNull(elements);
        Validate.isTrue(elements.size() > 0);
        int idx = randomInt(0, elements.size() - 1);
        Iterator<T> iter = elements.iterator();
        for (int i = 0; i < idx - 1; i++) {
            iter.next();
        }
        return iter.next();
    }

    public <T> Set<T> choose(Set<T> elements, int count) {
        Validate.notNull(elements);
        Validate.isTrue(elements.size() > 0);
        if (count > elements.size()) {
            count = elements.size();
        }
        Set<Integer> chosenIndexes = randomIntsDistinct(0, elements.size() - 1, count);
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

    public <T> T chooseOrCreateNew(Collection<T> elements,
                                   double oddsToCreateNew,
                                   Callable<T> createNewFunction) throws Exception {
        if (tryOdds(oddsToCreateNew)) {
            T t = createNewFunction.call();
            elements.add(t);
            return t;
        } else {
            return choose(elements);
        }
    }

    public String randomWords(int count) {
        Validate.isTrue(count > 0);
        if (dictionary == null) {
            _loadDictionary();
        }
        Set<String> words = choose(dictionary, count);
        StringBuilder s = new StringBuilder();
        words.forEach(word -> s.append(word).append(" "));
        return s.toString().trim();
    }

    public String getDictionaryFileName() {
        return config.dictionaryFileName;
    }

    public void setDictionaryFileName(String dictionaryFileName) {
        this.config.dictionaryFileName = dictionaryFileName;
    }

    // -------------------------------
    // protected methods
    // -------------------------------

    @SuppressWarnings("SimplifiableIfStatement")
    protected boolean tryOdds(double odds) {
        Validate.isTrue(odds >= 0.0 && odds <= 1.0);
        if (odds == 0.0) {
            return false;
        }
        return randomDouble(0.0, 1.0) <= odds;
    }

    protected void log(String s) {
        System.out.println(s);
    }

    // ------------------------
    // private methods
    // ------------------------

    private Long _nextLong(long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % n;
        }
        while (bits - val + (n - 1) < 0L);
        return val;
    }

    private void _loadDictionary() {
        dictionary = new HashSet<>();
        try {
            List<String> words = IOUtils.readLines(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(getDictionaryFileName()),
                    StandardCharsets.UTF_8);
            dictionary.addAll(words);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
