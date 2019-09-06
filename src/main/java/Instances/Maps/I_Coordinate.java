package Instances.Maps;

public interface I_Coordinate {

    /**
     * Returns the euclidean distance to another {@link I_Coordinate coordinate}. Should return 0 iff this.equals(other)
     * return true.
     * @param other a {@link I_Coordinate coordinate}.
     * @return the euclidean distance to another {@link I_Coordinate coordinate}.
     */
    float euclideanDistance(I_Coordinate other);

    /**
     * Returns the manhattan distance to another {@link I_Coordinate coordinate}. Should return 0 iff this.equals(other)
     * return true.
     * @param other a {@link I_Coordinate coordinate}.
     * @return the manhattan distance to another {@link I_Coordinate coordinate}.
     */
    float manhattanDistance(I_Coordinate other);
}
