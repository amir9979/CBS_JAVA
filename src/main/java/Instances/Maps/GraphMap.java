package Instances.Maps;

import java.util.HashMap;
import java.util.Map;

public class GraphMap implements I_Map {


    public GraphMap(String[] mapAsStrings){
        // blocking
    }


    private HashMap<I_Coordinate, I_MapCell> map;

    public GraphMap(HashMap<I_Coordinate, I_MapCell> map) {
        this.map = map;
    }

    @Override
    public I_MapCell getMapCell(I_Coordinate i_coordinate) {
        return null;
    }

    @Override
    public I_MapCell[] getNeighbors(I_MapCell mapCell) {
        return new I_MapCell[0];
    }

    @Override
    public boolean isValidCoordinate(I_Coordinate coordinate) {
        return this.map.containsKey(coordinate);
    }

}
