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

import com.grepcurl.Parent;
import org.junit.Test;

public class ParentChildTest {

    private static ObjectGenerator g = new IdAwareObjectGenerator();

    @Test
    public void testInfiniteRecursion() throws Exception {
        Parent o = g.generate(Parent.class, new SetterOverrides().add("setParent", () -> null));
        System.out.println(o);
    }

}
