package com.danbarrese.random.config;

import java.text.SimpleDateFormat;

public class GeneratorConfig {

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
    public boolean verbose = false;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    public String dictionaryFileName = "dict_250.txt";
    public int DEFAULT_SET_SIZE_MIN = 1;
    public int DEFAULT_SET_SIZE_MAX = 10;
    public FieldOverrides fieldOverrides = new FieldOverrides();


}