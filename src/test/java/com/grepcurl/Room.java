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
package com.grepcurl;

import java.util.Set;

public class Room {

    private Long id;
    private String name;
    private int width;
    private int height;
    private int depth;
    private Set<Chair> chairs;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public Set<Chair> getChairs() { return chairs; }
    public void setChairs(Set<Chair> chairs) { this.chairs = chairs; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Room{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", depth=").append(depth);
        sb.append(", chairs=").append(chairs);
        sb.append('}');
        return sb.toString();
    }

}
