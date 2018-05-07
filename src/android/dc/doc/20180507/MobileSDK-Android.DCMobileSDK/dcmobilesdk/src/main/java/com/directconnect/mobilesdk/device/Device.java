package com.directconnect.mobilesdk.device;

/**
 * Device identification
 *
 * Created by Francois Bergeon on 3/10/17.
 */
public class Device {
    private String name;
    private Class type;
    private String address;

    // Constructor
    public Device(String name, Class type, String address) {
        this.name = name;
        this.type = type;
        this.address = address;
    }

    // Property getters
    public String getName() { return name; }
    public Class getType() { return type; }
    public String getAddress() { return address; }
    public String toString() {
        return String.format("%s [%s]", name, address);
    }
}
