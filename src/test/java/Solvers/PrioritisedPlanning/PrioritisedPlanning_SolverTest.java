package Solvers.PrioritisedPlanning;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.MAPF_Instance;
import Instances.Maps.*;
import Solvers.AStar.SingleAgentAStar_Solver;
import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrioritisedPlanning_SolverTest {

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
    private I_Map mapEmpty = MapFactory.newSimple4Connected2D_GraphMap(map_2D_empty);

    Enum_MapCellType[][] map_2D_withPocket = {
            {e, w, e, w, e, w},
            {e, w, e, e, e, e},
            {w, w, e, w, w, e},
            {e, e, e, e, e, e},
            {e, e, w, e, w, w},
            {w, e, w, e, e, e},
    };
    private I_Map mapWithPocket = MapFactory.newSimple4Connected2D_GraphMap(map_2D_withPocket);

    private I_Coordinate coor12 = new Coordinate_2D(1,2);
    private I_Coordinate coor13 = new Coordinate_2D(1,3);
    private I_Coordinate coor14 = new Coordinate_2D(1,4);
    private I_Coordinate coor22 = new Coordinate_2D(2,2);
    private I_Coordinate coor24 = new Coordinate_2D(2,4);
    private I_Coordinate coor32 = new Coordinate_2D(3,2);
    private I_Coordinate coor33 = new Coordinate_2D(3,3);
    private I_Coordinate coor34 = new Coordinate_2D(3,4);

    private I_Coordinate coor11 = new Coordinate_2D(1,1);
    private I_Coordinate coor43 = new Coordinate_2D(4,3);
    private I_Coordinate coor53 = new Coordinate_2D(5,3);
    private I_Coordinate coor05 = new Coordinate_2D(0,5);

    private I_Coordinate coor04 = new Coordinate_2D(0,4);
    private I_Coordinate coor00 = new Coordinate_2D(0,0);
    private I_Coordinate coor01 = new Coordinate_2D(0,1);
    private I_Coordinate coor10 = new Coordinate_2D(1,0);

    private I_MapCell cell12 = mapCircle.getMapCell(coor12);
    private I_MapCell cell13 = mapCircle.getMapCell(coor13);
    private I_MapCell cell14 = mapCircle.getMapCell(coor14);
    private I_MapCell cell22 = mapCircle.getMapCell(coor22);
    private I_MapCell cell24 = mapCircle.getMapCell(coor24);
    private I_MapCell cell32 = mapCircle.getMapCell(coor32);
    private I_MapCell cell33 = mapCircle.getMapCell(coor33);
    private I_MapCell cell34 = mapCircle.getMapCell(coor34);

    private I_MapCell cell11 = mapCircle.getMapCell(coor11);
    private I_MapCell cell43 = mapCircle.getMapCell(coor43);
    private I_MapCell cell53 = mapCircle.getMapCell(coor53);
    private I_MapCell cell05 = mapCircle.getMapCell(coor05);

    private I_MapCell cell04 = mapCircle.getMapCell(coor04);
    private I_MapCell cell00 = mapCircle.getMapCell(coor00);
    private I_MapCell cell01 = mapCircle.getMapCell(coor01);
    private I_MapCell cell10 = mapCircle.getMapCell(coor10);

    private Agent agent33to12 = new Agent(0, coor33, coor12);
    private Agent agent12to33 = new Agent(1, coor12, coor33);
    private Agent agent53to05 = new Agent(2, coor53, coor05);
    private Agent agent43to11 = new Agent(3, coor43, coor11);
    private Agent agent04to00 = new Agent(4, coor04, coor00);
    private Agent agent00to10 = new Agent(5, coor00, coor10);
    private Agent agent10to00 = new Agent(6, coor10, coor00);

    InstanceBuilder_BGU builder = new InstanceBuilder_BGU();
    InstanceManager im = new InstanceManager(IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,"Instances"}),
            new InstanceBuilder_BGU(), new InstanceProperties(new MapDimensions(new int[]{6,6}),0f,new int[]{1}));

    private MAPF_Instance instanceEmpty1 = new MAPF_Instance("instanceEmpty", mapEmpty, new Agent[]{agent33to12, agent12to33, agent53to05, agent43to11, agent04to00});
    private MAPF_Instance instanceCircle1 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent33to12, agent12to33});
    private MAPF_Instance instanceCircle2 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent12to33, agent33to12});
    private MAPF_Instance instanceUnsolvable = new MAPF_Instance("instanceUnsolvable", mapWithPocket, new Agent[]{agent00to10, agent10to00});

    I_Solver ppSolver = new PrioritisedPlanning_Solver(new SingleAgentAStar_Solver());

    @BeforeEach
    void setUp() {

    }

    @Test
    void emptyMapValidityTest1() {
        MAPF_Instance testInstance = instanceEmpty1;
        Solution solved = ppSolver.solve(testInstance, new RunParameters());

        assertTrue(solved.isValidSolution());
    }

    @Test
    void circleMapValidityTest1() {
        MAPF_Instance testInstance = instanceCircle1;
        Solution solved = ppSolver.solve(testInstance, new RunParameters());

        assertTrue(solved.isValidSolution());
    }

    @Test
    void circleMapValidityTest2() {
        MAPF_Instance testInstance = instanceCircle2;
        Solution solved = ppSolver.solve(testInstance, new RunParameters());

        assertTrue(solved.isValidSolution());
    }

    @Test
    void unsolvableShouldBeInvalid() {
        MAPF_Instance testInstance = instanceUnsolvable;
        Solution solved = ppSolver.solve(testInstance, new RunParameters());

        assertNull(solved);
    }


}