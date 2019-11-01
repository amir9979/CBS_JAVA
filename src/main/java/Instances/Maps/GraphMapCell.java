package Instances.Maps;

import java.util.List;

/**
 * A single cell in a {@link GraphMap}. Represents a unique location in the graph.
 * Immutable beyond first initialization (First with a constructor, and then with {@link #setNeighbors(GraphMapCell[])}.
 * Equals and HashCode are not overridden, because the implementation of {@link GraphMap} and this class does not allow
 * duplicate instances of the same {@link GraphMapCell}. If an extending class wished to override these, it would be
 * best to use all fields, not just the {@link #coordinate}.
 */
public class GraphMapCell implements I_MapCell{

    /**
     * The type of the cell. The type could determine whether or not an agent can traverse or occupy a cell.
     */
    public final Enum_MapCellType cellType;
    public List<I_MapCell>  neighbors;
    public final I_Coordinate coordinate;

    public GraphMapCell(Enum_MapCellType cellType, I_Coordinate coordinate) {
        this.cellType = cellType;
        this.coordinate = coordinate;
        this.neighbors = null;
    }

    /**
     * Sets the cell's neighbors. All cells in the array should logically be non null.
     * Used during graph construction. Only the first call to this method on an instance affects the instance.
     * @param neighbors the cell's neighbors.
     * @throws NullPointerException if an element is null or the given array is null.
     */
    void setNeighbors(GraphMapCell[] neighbors) {
        this.neighbors = (this.neighbors == null ? List.of(neighbors) : this.neighbors);
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
     * Returns an UnmodifiableList of this cell's neighbors.
     * The amount of neighbors varies by map and connectivity.
     * Runs in O(1).
     * @return an UnmodifiableList of this cell's neighbors.
     */
    @Override
    public List<I_MapCell> getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns true iff other is contained in {@link #neighbors}. In particular, returns false if other==this.
     * @param other another {@link I_MapCell}.
     * @return true iff other is contained in {@link #neighbors}.
     */
    @Override
    public boolean isNeighbor(I_MapCell other) {
        boolean result = false;
        for (I_MapCell neighbor :
                neighbors) {
            result = result || (neighbor.equals(other));
        }
        return result;
    }

}
