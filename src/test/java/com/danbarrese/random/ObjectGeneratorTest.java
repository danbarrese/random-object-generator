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

import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

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

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
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

    @Test
    public void generate() throws Exception {
        ObjectGenerator g = new ObjectGenerator();
        MyObject2 a = g.generate(MyObject2.class);
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
        Assert.assertNotNull(a.getHeight());
    }

    @Test
    public void testCustomField() throws Exception {
        ObjectGenerator g = new ObjectGenerator();
        g.config.fieldOverrides.add(MyObject2.class, "name", () -> "blahhh");
        g.config.fieldOverrides.add(MySubSubObject.class, "dexterity", () -> "hahaha");

        MyObject2 a = g.generate(MyObject2.class);
        System.out.println(a.getColor());
        Assert.assertEquals("blahhh", a.getName());
        Assert.assertEquals("hahaha", a.getSub().getSubSub().getDexterity());
    }

}
