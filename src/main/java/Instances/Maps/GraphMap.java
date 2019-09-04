package Instances.Maps;

import java.util.HashMap;
import java.util.Map;

public class GraphMap implements I_Map {

    private HashMap<I_Coordinate, GraphMapCell> map;

    GraphMap(HashMap<I_Coordinate, GraphMapCell> map) {
        this.map = map;
    }

    //imp
    @Override
    public I_MapCell getMapCell(I_Coordinate coordinate) {
        return map.get(coordinate);
    }

    @Override
    public boolean isValidCoordinate(I_Coordinate coordinate) {
        return this.map.containsKey(coordinate);
    }

}
