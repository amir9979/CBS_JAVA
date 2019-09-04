package Instances.Maps;

import java.util.Objects;

public class Coordinate_2D implements I_Coordinate {

    private int x_value;
    private int y_value;



    public Coordinate_2D(int x_value, int y_value) {
        this.x_value = x_value;
        this.y_value = y_value;
    }

    public int getX_value() {
        return x_value;
    }

    public void setX_value(int x_value) {
        this.x_value = x_value;
    }

    public int getY_value() {
        return y_value;
    }

    public void setY_value(int y_value) {
        this.y_value = y_value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate_2D)) return false;
        Coordinate_2D that = (Coordinate_2D) o;
        return getX_value() == that.getX_value() &&
                getY_value() == that.getY_value();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX_value(), getY_value());
    }


    @Override
    public String toString() {
        return "Coordinate_2D{" +
                "x_value=" + x_value +
                ", y_value=" + y_value +
                '}';
    }

    public float euclideanDistance(I_Coordinate other) {
        return 0;
    }

    public float manhattanDistance(I_Coordinate other) {
        return 0;
    }
}
