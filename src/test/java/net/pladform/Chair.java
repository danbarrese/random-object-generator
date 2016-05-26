package net.pladform;

/**
 * @author Dan Barrese
 */
public class Chair {

    private boolean comfy;
    private double cost;

    public Chair() {
    }

    public Chair(boolean comfy) {
        this.comfy = comfy;
    }

    public Chair(boolean comfy, double cost) {
        this.comfy = comfy;
        this.cost = cost;
    }

    public boolean isComfy() { return comfy; }
    public void setComfy(boolean comfy) { this.comfy = comfy; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Chair{");
        sb.append("comfy=").append(comfy);
        sb.append(", cost=").append(cost);
        sb.append('}');
        return sb.toString();
    }

}
