package Instances.Maps;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFactory {

    /**
     * Generates a new 4-connected {@link GraphMap} from a square, 2D grid.
     *
     * Simple - Only 2 {@link Enum_MapCellType cell types} exist, {@link Enum_MapCellType#EMPTY} and
     * {@link Enum_MapCellType#WALL}. {@link Enum_MapCellType#EMPTY} cells are passable, and can only connect to other
     * {@link Enum_MapCellType#EMPTY} cells. {@link Enum_MapCellType#WALL} cells are impassable, and can not connect to
     * any other cell, so they will not be generated.
     * @param rectangle_2D_Map A rectangle grid representing a map, containing only {@link Enum_MapCellType#EMPTY} and
     *                      {@link Enum_MapCellType#WALL}. The length of its first dimension should correspond to the
     *                         original map's x dimension.
     * @return a new 4-connected {@link GraphMap}.
     */
    public static GraphMap newSimple4Connected2D_GraphMap(Enum_MapCellType[][] rectangle_2D_Map){
        int xLength = rectangle_2D_Map.length;
        int yLength = rectangle_2D_Map[0].length;
        GraphMapCell[][] cells = new GraphMapCell[xLength][yLength]; //rectangle map
        //generate all cells
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                if(rectangle_2D_Map[x][y] == Enum_MapCellType.EMPTY){
                    cells[x][y] = new GraphMapCell(rectangle_2D_Map[x][y], new Coordinate_2D(x, y));
                }
            }
        }
        HashMap<I_Coordinate, GraphMapCell> allCells = new HashMap<>(); //to be used for GraphMap constructor
        //connect cells to their neighbors (4-connected)
        ArrayList<GraphMapCell> neighbors = new ArrayList<>(4);
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                GraphMapCell currentCell = cells[x][y];
                if(cells[x][y] != null){
                    neighbors.clear();
                    //look for WEST neighbor
                    if(x-1 >= 0 && cells[x-1][y] != null){neighbors.add(cells[x-1][y]);}
                    //look for EAST neighbor
                    if(x+1 < xLength && cells[x+1][y] != null){neighbors.add(cells[x+1][y]);}
                    //look for NORTH neighbor
                    if(y-1 >= 0 && cells[x][y-1] != null){neighbors.add(cells[x][y-1]);}
                    //look for SOUTH neighbor
                    if(y+1 < xLength && cells[x][y+1] != null){neighbors.add(cells[x][y+1]);}
                    // set cell neighbors
                    currentCell.setNeighbors(neighbors.toArray(new GraphMapCell[0]));
                    // add to allCells
                    allCells.put(currentCell.coordinate, currentCell);
                }
            }
        }
        return new GraphMap(allCells);
    }

    /* nicetohave
    public static GraphMap newSimple8Connected2D_GraphMap(Enum_MapCellType[][] map_2D){
        return null;
    }
    */

    /* nicetohave
    public static GraphMap newSimple6Connected3D_GraphMap(Enum_MapCellType[][] map_2D){
        return null;
    }
    */

}
