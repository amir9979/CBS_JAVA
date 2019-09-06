package Instances.Maps;

import java.util.Objects;

/**
 * A data type which represents a coordinate in two-dimensional space.
 */
public class Coordinate_2D implements I_Coordinate {

    private final int x_value;
    private final int y_value;

    public Coordinate_2D(int x_value, int y_value) {
        this.x_value = x_value;
        this.y_value = y_value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate_2D that = (Coordinate_2D) o;
        return x_value == that.x_value &&
                y_value == that.y_value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x_value, this.y_value);
    }

    @Override
    public String toString() {
        return "Coordinate_2D{" +
                "x_value=" + this.x_value +
                ", y_value=" + this.y_value +
                '}';
    }

    public float euclideanDistance(I_Coordinate other) {
        //if (this == other) return 0; shouldn't really happen. Even if it will, returned value will be the same.
        if (other == null || getClass() != other.getClass()) return -1;
        Coordinate_2D that = (Coordinate_2D) other;
        return (float)Math.sqrt(
                    (this.y_value-that.y_value)*(this.y_value-that.y_value) +
                    (this.x_value-that.x_value)*(this.x_value-that.x_value)    );
    }

    public int manhattanDistance(I_Coordinate other) {
        //if (this == other) return 0; shouldn't really happen. Even if it will, returned value will be the same.
        if (other == null || getClass() != other.getClass()) return -1;
        Coordinate_2D that = (Coordinate_2D) other;
        return Math.abs(this.y_value-that.y_value) + Math.abs(this.x_value-that.x_value);
    }
}
