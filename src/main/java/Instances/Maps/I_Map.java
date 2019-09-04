package Instances.Maps;

import java.util.HashMap;

public interface I_Map {

    I_MapCell getMapCell(I_Coordinate i_coordinate);

    boolean isValidCoordinate(I_Coordinate i_coordinate);


}
