package net.pladform.random;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }

        public Double getLength() { return length; }
        public void setLength(Double length) { this.length = length; }

        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }

        protected String getHeight() { return height; }
        protected void setHeight(String height) { this.height = height; }

        public MySubObject getSub() { return sub; }
        public void setSub(MySubObject sub) { this.sub = sub; }
    }

    public static class MyObject2 extends MyObject {
        private String color;
        public MyObject2() {
            super();
        }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class MySubObject {
        private String strength;
        private MySubSubObject subSub;
        public MySubObject() {
        }
        public String getStrength() { return strength; }
        public void setStrength(String strength) { this.strength = strength; }

        public MySubSubObject getSubSub() { return subSub; }
        public void setSubSub(MySubSubObject subSub) { this.subSub = subSub; }
    }

    public static class MySubSubObject {
        private String dexterity;
        public MySubSubObject() {
        }
        public String getDexterity() { return dexterity; }
        public void setDexterity(String dexterity) { this.dexterity = dexterity; }
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
        Map<String, Callable> fns = new HashMap<>();
        fns.put("setName", () -> "blahhh");
        MyObject2 a = objectGenerator.generate(MyObject2.class, fns);
        System.out.println(a.getColor());
        Assert.assertEquals("blahhh", a.getName());
    }

}
