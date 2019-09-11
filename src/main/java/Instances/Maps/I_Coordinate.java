package Instances.Maps;

/**
 * An I_Coordinate represents a unique location in Euclidean space.
 */
public interface I_Coordinate {
    // todo generalize to include non-euclidean space? replace methods with one abstract h() function?
    /**
     * Returns the euclidean distance to another {@link I_Coordinate coordinate}. Should return 0 iff this.equals(other)
     * return true.  If other is null, or is not of the same runtime type as this, returns -1.
     * @param other a {@link I_Coordinate coordinate}.
     * @return the euclidean distance to another {@link I_Coordinate coordinate}. If other is null, or is not of the
     * same runtime type as this, returns -1.
     */
    float euclideanDistance(I_Coordinate other);

    /**
     * Returns the manhattan distance to another {@link I_Coordinate coordinate}. Should return 0 iff this.equals(other)
     * return true.  If other is null, or is not of the same runtime type as this, returns -1.
     * @param other a {@link I_Coordinate coordinate}.
     * @return the manhattan distance to another {@link I_Coordinate coordinate}.  If other is null, or is not of the
     * same runtime type as this, returns -1.
     */
    int manhattanDistance(I_Coordinate other);
}
