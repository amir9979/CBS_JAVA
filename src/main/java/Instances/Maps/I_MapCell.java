package Instances.Maps;

import java.util.List;

public interface I_MapCell {

    /**
     * Returns the type of the cell.
     * @return the type of the cell.
     */
    Enum_MapCellType getType();

    /**
     * Returns an array that contains references to this cell's neighbors. Should not include this.
     * The amount of neighbors varies by map and connectivity.
     * @return an array that contains references to this cell's neighbors. Should not include this.
     */
    List<I_MapCell> getNeighbors();

    /**
     * returns the cell's coordinate.
     * @return the cell's coordinate.
     */
    I_Coordinate getCoordinate();

    /**
     * Return true iff other is a neighbor of this.
     * @param other another {@link I_MapCell}.
     * @return true iff other is a neighbor of this.
     */
    boolean isNeighbor(I_MapCell other);

}
