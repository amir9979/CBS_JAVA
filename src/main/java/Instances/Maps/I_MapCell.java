package Instances.Maps;

public interface I_MapCell {

    Enum_MapCellType getType();

    I_MapCell[] getNeighbors();

    I_Coordinate getCoordinate();

}
