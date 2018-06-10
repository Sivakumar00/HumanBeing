package siva.com.weengineers;

/**
 * Created by MANIKANDAN on 18-02-2018.
 */

public class Pool {
    String name,destination,vehicle,phone;

    public Pool() {
    }

    public Pool(String name, String destination, String vehicle, String phone) {
        this.name = name;
        this.destination = destination;
        this.vehicle = vehicle;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
