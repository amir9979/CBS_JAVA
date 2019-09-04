package Instances.Maps;

import java.util.Arrays;

public class GraphMapCell implements I_MapCell{

    public final Enum_MapCellType cellType;
    private GraphMapCell[] neighbors;
    public final I_Coordinate coordinate;

    public GraphMapCell(Enum_MapCellType cellType, I_Coordinate coordinate) {
        this.cellType = cellType;
        this.coordinate = coordinate;
        this.neighbors = null;
    }

    /**
     * Sets the cell's neighbors. All cells in the array should logically be non null.
     * Used during graph construction. Only the first call to this method on an instance affects it.
     * @param neighbors the cell's neighbors.
     */
    void setNeighbors(GraphMapCell[] neighbors) {
        this.neighbors = (this.neighbors == null ? neighbors : this.neighbors);
    }

    /**
     * Returns the type of the cell.
     * @return the type of the cell.
     */
    @Override
    public Enum_MapCellType getType() {
        return cellType;
    }

    /**
     * returns the cell's coordinate.
     * @return the cell's coordinate.
     */
    @Override
    public I_Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns an array (copy) that contains references to this cell's neighbors.
     * The amount of neighbors varies by map and connectivity.
     * @return an array (copy) that contains references to this cell's neighbors.
     */
    @Override
    public I_MapCell[] getNeighbors() {
        return Arrays.copyOf(neighbors, neighbors.length);
    }
}
