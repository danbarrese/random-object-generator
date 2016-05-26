package net.pladform;

import java.util.Set;

/**
 * @author Dan Barrese
 */
public class House {

    private String address;
    private Set<Room> rooms;

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Set<Room> getRooms() { return rooms; }
    public void setRooms(Set<Room> rooms) { this.rooms = rooms; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("House{");
        sb.append("address='").append(address).append('\'');
        sb.append(", rooms=").append(rooms);
        sb.append('}');
        return sb.toString();
    }

}
