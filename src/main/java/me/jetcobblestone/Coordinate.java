package me.jetcobblestone;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Coordinate {
    double x;
    double y;
    double z;
    double pitch;
    double yaw;

    public Coordinate(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public Coordinate(double x, double y, double z, double pitch, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
