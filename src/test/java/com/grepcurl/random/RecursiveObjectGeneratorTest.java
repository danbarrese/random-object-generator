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

import java.util.List;

public class RecursiveObjectGeneratorTest {

    public static class MyObject {
        private MyOtherObject myOtherObject;
        private List<MyOtherObject> myOtherObjectList;

        public MyObject() {
        }

        public MyOtherObject getMyOtherObject() {
            return myOtherObject;
        }

        public void setMyOtherObject(MyOtherObject myOtherObject) {
            this.myOtherObject = myOtherObject;
        }

        public List<MyOtherObject> getMyOtherObjectList() {
            return myOtherObjectList;
        }

        public void setMyOtherObjectList(
                List<MyOtherObject> myOtherObjectList) {
            this.myOtherObjectList = myOtherObjectList;
        }
    }

    public static class MyOtherObject {
        private MyObject myObject;

        public MyObject getMyObject() {
            return myObject;
        }

        public void setMyObject(MyObject myObject) {
            this.myObject = myObject;
        }
    }

    ObjectGenerator objectGenerator = new IdAwareObjectGenerator();

    @Test
    public void generate() throws Exception {
        MyObject myObject = objectGenerator.generate(MyObject.class);
        Assert.assertTrue(myObject.myOtherObject.myObject == myObject);
        for (MyOtherObject other : myObject.myOtherObjectList) {
            Assert.assertTrue(other.myObject == myObject);
        }
    }

}
