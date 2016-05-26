package net.pladform;

/**
 * @author Dan Barrese
 */
public class Parent {

    private Child child;

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Parent{");
        sb.append("child=").append(child);
        sb.append('}');
        return sb.toString();
    }

}
