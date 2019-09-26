package Instances.Maps;

import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a {@link I_Map map} as an abstract graph. This implementation can, in principle, support any domain -
 * maps representing n dimensional space, any connectivity function, disjoint sub-graphs.
 *
 * Space complexity:
 * This implementation requires the entire graph to be built at initialization. For very large, sparse maps, this may
 * pose a space complexity challenge. Example: A 1000x1000x1000 map with just one agent, whose source and target are
 * adjacent.
 */
public class GraphMap implements I_Map {

    private HashMap<I_Coordinate, GraphMapCell> allGraphCells;

    /**
     * Initialization is done through {@link MapFactory}.
     * @param allGraphCells a {@link HashMap} containing all cells in the graph.
     */
    GraphMap(HashMap<I_Coordinate, GraphMapCell> allGraphCells) {
        this.allGraphCells = allGraphCells;
    }

    /**
     * Returns the {@link GraphMapCell} for the given {@link I_Coordinate}.
     * @param coordinate the {@link I_Coordinate} of the {@link GraphMapCell}.
     * @return the {@link GraphMapCell} for the given {@link I_Coordinate}.
     */
    @Override
    public GraphMapCell getMapCell(I_Coordinate coordinate) {
        return allGraphCells.get(coordinate);
    }

    @Override
    public boolean isValidCoordinate(I_Coordinate coordinate) {
        return this.allGraphCells.containsKey(coordinate);
    }

    public int getNumMapCells(){
        return allGraphCells.size();
    }


}
