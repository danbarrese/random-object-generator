package net.pladform.random;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ObjectGeneratorTest {

    public static class MyObject {
        private Long id;
        private String name;
        private String height;
        private Integer count;

        public MyObject() {
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }

        protected String getHeight() { return height; }
        protected void setHeight(String height) { this.height = height; }
    }

    public static class MyObject2 extends MyObject {
        private String color;
        public MyObject2() {
            super();
        }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    ObjectGenerator objectGenerator = new IdAwareObjectGenerator();

    @Test
    public <T> void generate() throws Exception {
        MyObject2 a = objectGenerator.generate(MyObject2.class);
        Assert.assertNotNull(a.getId());
        Assert.assertNotNull(a.getName());
        Assert.assertNotNull(a.getCount());
        Assert.assertNotNull(a.getColor());
        Assert.assertNull(a.getHeight());
        System.out.println(a.getId());
    }

}
