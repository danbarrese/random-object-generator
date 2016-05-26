package net.pladform;

/**
 * @author Dan Barrese
 */
public class Child {

    private Parent parent;

    public Parent getParent() { return parent; }
    public void setParent(Parent parent) { this.parent = parent; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Child{");
        sb.append("parent=").append(parent);
        sb.append('}');
        return sb.toString();
    }

}
