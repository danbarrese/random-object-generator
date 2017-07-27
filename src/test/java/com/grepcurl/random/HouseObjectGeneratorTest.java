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

import com.grepcurl.House;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

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
        House o = g.generate(House.class, new SetterOverrides().add("setName", () -> "blahhh"));
        System.out.println(o);
    }

}
