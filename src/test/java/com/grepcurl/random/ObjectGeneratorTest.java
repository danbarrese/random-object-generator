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

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ObjectGeneratorTest {

    public static class MyObject {
        private Long id;
        private String name;
        private String height;
        private Integer count;
        private Double length;
        private Date date;
        private MySubObject sub;

        public MyObject() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Double getLength() {
            return length;
        }

        public void setLength(Double length) {
            this.length = length;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        protected String getHeight() {
            return height;
        }

        protected void setHeight(String height) {
            this.height = height;
        }

        public MySubObject getSub() {
            return sub;
        }

        public void setSub(MySubObject sub) {
            this.sub = sub;
        }
    }

    public static class MyObject2 extends MyObject {
        private String color;

        public MyObject2() {
            super();
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class MySubObject {
        private String strength;
        private MySubSubObject subSub;

        public MySubObject() {
        }

        public String getStrength() {
            return strength;
        }

        public void setStrength(String strength) {
            this.strength = strength;
        }

        public MySubSubObject getSubSub() {
            return subSub;
        }

        public void setSubSub(MySubSubObject subSub) {
            this.subSub = subSub;
        }
    }

    public static class MySubSubObject {
        private String dexterity;

        public MySubSubObject() {
        }

        public String getDexterity() {
            return dexterity;
        }

        public void setDexterity(String dexterity) {
            this.dexterity = dexterity;
        }
    }

    ObjectGenerator objectGenerator = new IdAwareObjectGenerator();

    @Test
    public void generate() throws Exception {
        MyObject2 a = objectGenerator.generate(MyObject2.class);
        Assert.assertNotNull(a.getId());
        Assert.assertNotNull(a.getName());
        Assert.assertNotNull(a.getCount());
        Assert.assertNotNull(a.getLength());
        Assert.assertNotNull(a.getColor());
        Assert.assertNotNull(a.getDate());
        Assert.assertNotNull(a.getSub());
        Assert.assertNotNull(a.getSub().getStrength());
        Assert.assertNotNull(a.getSub().getSubSub());
        Assert.assertNotNull(a.getSub().getSubSub().getDexterity());
        Assert.assertNull(a.getHeight());
    }

    @Test
    public void testCustomFunction() throws Exception {
        SetterOverrides overrides = new SetterOverrides();
        overrides.add(MyObject2.class, "setName", () -> "blahhh");
        overrides.add(MySubSubObject.class, "setDexterity", () -> "hahaha");

        MyObject2 a = objectGenerator.generate(MyObject2.class, overrides);
        System.out.println(a.getColor());
        Assert.assertEquals("blahhh", a.getName());
        Assert.assertEquals("hahaha", a.getSub().getSubSub().getDexterity());
    }

}
