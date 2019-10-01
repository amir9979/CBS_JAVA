package Solvers.AStar;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.MAPF_Instance;
import Instances.Maps.Coordinate_2D;
import Instances.Maps.Enum_MapCellType;
import Instances.Maps.I_Map;
import Instances.Maps.MapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingleAgentAStar_SolverTest {

    private final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    private final Enum_MapCellType w = Enum_MapCellType.WALL;
    private Enum_MapCellType[][] map_2D_circle = {
            {w, w, w, w, w, w},
            {w, w, e, e, e, w},
            {w, w, e, w, e, w},
            {w, w, e, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };
    private I_Map mapCircle = MapFactory.newSimple4Connected2D_GraphMap(map_2D_circle);

    Enum_MapCellType[][] map_2D_empty = {
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
    };
    private I_Map mapOpen = MapFactory.newSimple4Connected2D_GraphMap(map_2D_circle);

    private Agent agent43to21 = new Agent(0, new Coordinate_2D(4,3), new Coordinate_2D(2, 1));
    private Agent agent53to05 = new Agent(1, new Coordinate_2D(5,3), new Coordinate_2D(0, 5));

    InstanceBuilder_BGU builder = new InstanceBuilder_BGU();
    InstanceManager im = new InstanceManager(IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,"Instances"}),
            new InstanceBuilder_BGU(), new InstanceProperties(new int[]{16,16},0,7,"-"));

    private MAPF_Instance instanceCircle = builder.getInstance();
    private MAPF_Instance instanceOpen;

    @BeforeEach
    void setUp() {

        instanceCircle
    }

    @Test
    void solve() {

    }
}