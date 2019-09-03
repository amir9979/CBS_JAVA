package Instances.Maps;

import java.util.HashMap;

public interface I_Map {

    I_MapCell getMapCell(I_Coordinate i_coordinate);

    I_MapCell[] getNeighbors(I_MapCell mapCell);

    boolean isValidCoordinate(I_Coordinate i_coordinate);



}
