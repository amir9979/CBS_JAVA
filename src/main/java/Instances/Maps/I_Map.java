package Instances.Maps;

import java.util.HashMap;

public interface I_Map {

    /**    /**
     * Returns the {@link I_MapCell map cell} for the given {@link I_Coordinate}.
     * @param i_coordinate the {@link I_Coordinate} of the {@link I_MapCell map cell}.
     * @return the {@link I_MapCell map cell} for the given {@link I_Coordinate}.
     */
    I_MapCell getMapCell(I_Coordinate i_coordinate);

    /**
     * @param i_coordinate the {@link I_Coordinate} to check.
     * @return true if the given coordinate is valid for this map, else false.
     */
    boolean isValidCoordinate(I_Coordinate i_coordinate);

}
