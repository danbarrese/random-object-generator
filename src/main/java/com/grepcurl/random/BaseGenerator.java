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
package com.grepcurl.random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Responsible for generation of random Strings, Longs, ints, etc.
 */
@SuppressWarnings({ "unchecked", "unused", "FieldCanBeLocal" })
public class BaseGenerator {

    public String CHARACTER_SET = " \tabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890`~!@#$%^&*()_+[]\\{}|;':\",.<>/?";
    public String EMAIL_CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    public int DEFAULT_STRING_LENGTH_MIN = 1;
    public int DEFAULT_STRING_LENGTH_MAX = 25;
    public int DEFAULT_INT_MIN = 0;
    public int DEFAULT_INT_MAX = 1000;
    public long DEFAULT_LONG_MIN = -1000000L;
    public long DEFAULT_LONG_MAX = 1000000L;
    public double DEFAULT_DOUBLE_MIN = 0.0;
    public double DEFAULT_DOUBLE_MAX = 1000000.0;
    public String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public double DEFAULT_CHANCE_OF_NULL_STRING = 0.0;
    public double DEFAULT_CHANCE_OF_NULL_INT = 0.0;
    public double DEFAULT_CHANCE_OF_NULL_LONG = 0.0;
    public double DEFAULT_CHANCE_OF_NULL_DOUBLE = 0.0;
    public double DEFAULT_CHANCE_OF_NULL_BOOLEAN = 0.0;
    public double DEFAULT_CHANCE_OF_NULL_CHAR = 0.0;
    public double DEFAULT_CHANCE_OF_NULL_DATE = 0.0;

    private Random _random;
    private Map<String, AtomicLong> _idGenerator;
    private DateTimeFormatter _dateTimeFormatter = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT);
    private Set<String> _dictionary;
    private String _dictionaryFileName = "dict_60000.txt";
    private static SimpleDateFormat _sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    public boolean verbose = false;

    // ------------------------
    // constructors
    // ------------------------

    public BaseGenerator() {
        _random = new Random(System.currentTimeMillis());
        _idGenerator = new HashMap<>();
    }

    // ------------------------
    // public methods
    // ------------------------

    public <T> T random(Class<T> type) {
        Validate.notNull(type);
        if (type.equals(String.class)) {
            return (T) randomWords(1);
        }
        else if (type.equals(Integer.class) || type.equals(int.class)) {
            return (T) randomInt();
        }
        else if (type.equals(Long.class) || type.equals(long.class)) {
            return (T) randomLong();
        }
        else if (type.equals(Double.class) || type.equals(double.class)) {
            return (T) randomDouble();
        }
        else if (type.equals(Date.class)) {
            return (T) randomDate();
        }
        else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (T) randomBoolean();
        }
        else if (type.equals(Character.class) || type.equals(char.class)) {
            return (T) randomChar();
        }
        else if (type.equals(BigDecimal.class)) {
            return (T) randomBigDecimal();
        }
        else if (type.equals(Serializable.class)) {
            return (T) nextId(type);
        }
        else {
            throw new IllegalArgumentException("Don't know how to generate a random " + type.getName());
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public <T> boolean isBaseType(Class<T> type) {
        Validate.notNull(type);
        if (type.equals(String.class)) {
            return true;
        }
        else if (type.equals(Integer.class) || type.equals(int.class)) {
            return true;
        }
        else if (type.equals(Long.class) || type.equals(long.class)) {
            return true;
        }
        else if (type.equals(Double.class) || type.equals(double.class)) {
            return true;
        }
        else if (type.equals(Date.class)) {
            return true;
        }
        else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return true;
        }
        else if (type.equals(Character.class) || type.equals(char.class)) {
            return true;
        }
        else if (type.equals(BigDecimal.class)) {
            return true;
        }
        else if (type.equals(Serializable.class)) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isBaseType(Object o) {
        Validate.notNull(o);
        return isBaseType(o.getClass());
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean tryOdds(double odds) {
        Validate.isTrue(odds >= 0.0 && odds <= 1.0);
        if (odds == 0.0) {
            return false;
        }
        return randomDouble(0.0, 1.0) <= odds;
    }

    public String randomString() {
        int len = randomInt(DEFAULT_STRING_LENGTH_MIN, DEFAULT_STRING_LENGTH_MAX);
        return randomString(len);
    }

    public String randomString(int len) {
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_STRING)) {
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
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_STRING)) {
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
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_CHAR)) {
            return null;
        }
        return EMAIL_CHAR_LIST.charAt(randomInt(0, EMAIL_CHAR_LIST.length() - 1));
    }

    public Character randomChar() {
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_CHAR)) {
            return null;
        }
        return CHARACTER_SET.charAt(randomInt(0, CHARACTER_SET.length() - 1));
    }

    public Integer randomInt() {
        return randomInt(DEFAULT_INT_MIN, DEFAULT_INT_MAX);
    }

    public Integer randomInt(int lowerBound, int upperBound) {
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_INT)) {
            return null;
        }
        Validate.isTrue(upperBound >= lowerBound);
        int i = upperBound - lowerBound + 1;
        if (upperBound >= 0 && i <= 0) {
            long l = randomLong(lowerBound, upperBound);
            return (int) l;
        }
        return _random.nextInt(i) + lowerBound;
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
        return randomLong(DEFAULT_LONG_MIN, DEFAULT_LONG_MAX);
    }

    public Long randomLong(long lowerBound, long upperBound) {
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_LONG)) {
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
        return randomDouble(DEFAULT_DOUBLE_MIN, DEFAULT_DOUBLE_MAX);
    }

    public BigDecimal randomBigDecimal() {
        return BigDecimal.valueOf(randomDouble(DEFAULT_DOUBLE_MIN, DEFAULT_DOUBLE_MAX));
    }

    public Double randomProbability() {
        return _random.nextDouble();
    }

    public Double randomDouble(double lowerBound, double upperBound) {
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_DOUBLE)) {
            return null;
        }
        Validate.isTrue(upperBound >= lowerBound);
        return lowerBound + (upperBound - lowerBound) * _random.nextDouble();
    }

    public Long nextId() {
        return nextId("default");
    }

    public Long nextId(String arbitraryGeneratorName) {
        Validate.notNull(arbitraryGeneratorName);
        return _idGenerator.computeIfAbsent(arbitraryGeneratorName, s -> new AtomicLong(1)).getAndIncrement();
    }

    public Long nextId(Class<?> type) {
        Validate.notNull(type);
        return nextId(type.getName());
    }

    public Date randomDate(String fromDate, String toDate) {
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_DATE)) {
            return null;
        }
        Validate.notNull(fromDate);
        Validate.notNull(toDate);
        long lowerBound = _dateTimeFormatter.parseDateTime(fromDate).getMillis();
        DateTime upperBoundDateTime = _dateTimeFormatter.parseDateTime(toDate);
        long upperBound = upperBoundDateTime.getMillis();
        long range = upperBound - lowerBound + 1;
        return upperBoundDateTime.minus(randomLong(0, range)).toDate();
    }

    public Date randomDate(String fromDate) {
        Validate.notNull(fromDate);
        DateTime now = new DateTime(new Date());
        return randomDate(fromDate, now.toString(_dateTimeFormatter));
    }

    public Date randomDate() {
        DateTime epoch = new DateTime(0L);
        DateTime now = new DateTime(new Date());
        return randomDate(epoch.toString(_dateTimeFormatter), now.toString(_dateTimeFormatter));
    }

    public Date randomDateInLastNDays(int n) {
        Date temp = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(temp);
        c.add(Calendar.DAY_OF_YEAR, randomInt(-1 * n, -1));
        Date startDate = c.getTime();

        String startDateString = _sdf.format(startDate);
        try {
            return _sdf.parse(startDateString);
        }
        catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Internal error formatting date during JUnit test.");
        }
    }

    public Boolean randomBoolean() {
        if (tryOdds(DEFAULT_CHANCE_OF_NULL_BOOLEAN)) {
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
        }
        else if (elements instanceof Set) {
            return choose((Set<T>) elements);
        }
        else {
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
        }
        else {
            return choose(elements);
        }
    }

    public String randomWords(int count) {
        Validate.isTrue(count > 0);
        if (_dictionary == null) {
            _loadDictionary();
        }
        Set<String> words = choose(_dictionary, count);
        StringBuilder s = new StringBuilder();
        words.forEach(word -> s.append(word).append(" "));
        return s.toString().trim();
    }

    public String getDictionaryFileName() {
        return _dictionaryFileName;
    }

    public void setDictionaryFileName(String dictionaryFileName) {
        this._dictionaryFileName = dictionaryFileName;
    }
    
    public void log(String s) {
        System.out.println(s);
    }

    // ------------------------
    // private methods
    // ------------------------

    private Long _nextLong(long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (_random.nextLong() << 1) >>> 1;
            val = bits % n;
        }
        while (bits - val + (n - 1) < 0L);
        return val;
    }

    private void _loadDictionary() {
        _dictionary = new HashSet<>();
        try {
            List<String> words = IOUtils.readLines(Thread.currentThread().getContextClassLoader().getResourceAsStream(
                    getDictionaryFileName()));
            words.forEach(_dictionary::add);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
