package Instances.Maps;

public interface I_MapCell {

    /**
     * Returns the type of the cell.
     * @return the type of the cell.
     */
    Enum_MapCellType getType();

    /**
     * Returns an array that contains references to this cell's neighbors.
     * The amount of neighbors varies by map and connectivity.
     * @return an array that contains references to this cell's neighbors.
     */
    I_MapCell[] getNeighbors();

    /**
     * returns the cell's coordinate.
     * @return the cell's coordinate.
     */
    I_Coordinate getCoordinate();

}
