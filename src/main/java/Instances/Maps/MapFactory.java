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
     *                         number of rows, i.e. the original map's y dimension.
     * @return a new 4-connected {@link GraphMap}.
     */
    public static GraphMap newSimple4Connected2D_GraphMap(Enum_MapCellType[][] rectangle_2D_Map){
        int numRows = rectangle_2D_Map.length;
        int numColumns = rectangle_2D_Map[0].length;
        GraphMapCell[][] cells = new GraphMapCell[numRows][numColumns]; //rectangle map
        //generate all cells
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                if(rectangle_2D_Map[row][column] == Enum_MapCellType.EMPTY){
                    cells[row][column] = new GraphMapCell(rectangle_2D_Map[row][column],
                            /* note how row <=> coordinate.y */
                            new Coordinate_2D(column, row));
                }
            }
        }
        HashMap<I_Coordinate, GraphMapCell> allCells = new HashMap<>(); //to be used for GraphMap constructor
        //connect cells to their neighbors (4-connected)
        ArrayList<GraphMapCell> neighbors = new ArrayList<>(4);
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                GraphMapCell currentCell = cells[row][column];
                if(cells[row][column] != null){
                    neighbors.clear();
                    //look for NORTH neighbor
                    if(row-1 >= 0 && cells[row-1][column] != null){neighbors.add(cells[row-1][column]);}
                    //look for SOUTH neighbor
                    if(row+1 < numRows && cells[row+1][column] != null){neighbors.add(cells[row+1][column]);}
                    //look for WEST neighbor
                    if(column-1 >= 0 && cells[row][column-1] != null){neighbors.add(cells[row][column-1]);}
                    //look for EAST neighbor
                    if(column+1 < numRows && cells[row][column+1] != null){neighbors.add(cells[row][column+1]);}
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
