package Maps;

import Instances.Maps.Coordinate_2D;
import Instances.Maps.Enum_MapCellType;
import Instances.Maps.GraphMap;
import Instances.Maps.MapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapFactoryTest {

    private final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    private final Enum_MapCellType w = Enum_MapCellType.WALL;

    /*  =Sample 2D Maps=  */

    Enum_MapCellType[][] map_2D_1Cell_middle = {
            {w, w, w, w, w, w},
            {w, e, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };

    Enum_MapCellType[][] map_2D_1Cell_fringe = {
            {w, w, w, w, w, w},
            {e, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };

    Enum_MapCellType[][] map_2D_2Cells_middle = {
            {w, w, w, w, w, w},
            {w, w, w, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };

    Enum_MapCellType[][] map_2D_2Cells_fringe = {
            {w, w, w, w, e, w},
            {w, w, w, w, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };

    Enum_MapCellType[][] map_2D_3Cells_line = {
            {w, w, w, e, w, w},
            {w, w, w, e, w, w},
            {w, w, w, e, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };

    Enum_MapCellType[][] map_2D_4cells_clump = {
            {w, w, w, w, w, w},
            {w, w, w, e, e, w},
            {w, w, w, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };

    Enum_MapCellType[][] map_2D_circle = {
            {w, w, w, w, w, w},
            {w, w, e, e, e, w},
            {w, w, e, w, e, w},
            {w, w, e, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };

    Enum_MapCellType[][] map_2D_empty = {
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
    };

    @BeforeEach
    void setUp() {

    }

    /*  = newSimple4Connected2D_GraphMap =  */

    @Test
    void newSimple4Connected2D_GraphMap() {
        //verify that maps are created with the expected graph structure
        test_map_2D_1Cell_middle(MapFactory.newSimple4Connected2D_GraphMap(map_2D_1Cell_middle));
    }

    void checkGraphMapProperties(GraphMap map, int numCells, Coordinate_2D[] containsCoordinates){
        assertEquals(numCells, map.getNumMapCells());
        for (Coordinate_2D coor:
            containsCoordinates){
            assertTrue(map.isValidCoordinate(coor));
        }
    }

    void test_map_2D_1Cell_middle(GraphMap graphMap){

    }


}