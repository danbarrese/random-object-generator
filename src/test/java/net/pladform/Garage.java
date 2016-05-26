package net.pladform;

/**
 * @author Dan Barrese
 */
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
