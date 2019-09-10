package Instances.Maps;

import java.util.Arrays;

/**
 * A single cell in a {@link GraphMap}. Represents a unique location in the graph.
 * Immutable beyond first initialization (First with a constructor, and then with {@link #setNeighbors(GraphMapCell[])}.
 * Equals and HashCode are not overridden, because the implementation of {@link GraphMap} and this class does not allow
 * duplicate instances of the same {@link GraphMapCell}.
 */
public class GraphMapCell implements I_MapCell{

    /**
     * The type of the cell. The type could determine whether or not an agent can traverse or occupy a cell.
     */
    public final Enum_MapCellType cellType;
    private GraphMapCell[] neighbors;
    public final I_Coordinate coordinate;

    GraphMapCell(Enum_MapCellType cellType, I_Coordinate coordinate) {
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
     * Runs in O({@link #neighbors}.length).
     * @return an array (copy) that contains references to this cell's neighbors.
     */
    @Override
    public I_MapCell[] getNeighbors() {
        return Arrays.copyOf(neighbors, neighbors.length);
    }

    /**
     * Returns true iff other is contained in {@link #neighbors}. In particular, returns false if other==this.
     * Equality is checked through reference equality, because the implementation of {@link GraphMap} and this class
     * does not allow duplicate instances of the same {@link GraphMapCell}.
     * @param other another {@link I_MapCell}.
     * @return true iff other is contained in {@link #neighbors}.
     */
    @Override
    public boolean isNeighbor(I_MapCell other) {
        boolean result = false;
        for (GraphMapCell neighbor :
                neighbors) {
            result = result || (neighbor == other);
        }
        return result;
    }

}
