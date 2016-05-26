package net.pladform;

import java.util.Set;

/**
 * @author Dan Barrese
 */
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
