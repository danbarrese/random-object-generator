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
package com.danbarrese;

public class Garage extends Room {

    private int cars;

    public int getCars() { return cars; }
    public void setCars(int cars) { this.cars = cars; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Garage{");
        sb.append("cars=").append(cars);
        sb.append('}');
        return sb.toString();
    }

}
