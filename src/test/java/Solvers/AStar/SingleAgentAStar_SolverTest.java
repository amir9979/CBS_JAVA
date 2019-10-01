package Solvers.AStar;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.MAPF_Instance;
import Instances.Maps.*;
import Solvers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
            new InstanceBuilder_BGU(), new InstanceProperties(new int[]{6,6},0,1,"-"));

    private MAPF_Instance instanceEmpty = im.getNextInstance();
    private MAPF_Instance instance1stepSolution = im.getNextInstance();
    private MAPF_Instance instanceCircle1 = im.getNextInstance();
    private MAPF_Instance instanceCircle2 = im.getNextInstance();

    I_Solver aStar = new SingleAgentAStar_Solver();

    @BeforeEach
    void setUp() {

    }

    @Test
    void solve() {
        MAPF_Instance testInstance = instance1stepSolution;
        Solution s = aStar.solve(testInstance, new RunParameters());

        Map<Agent, SingleAgentPlan> plans = new HashMap<>();
        SingleAgentPlan plan = new SingleAgentPlan(testInstance.agents.get(0));
        I_MapCell cell = testInstance.map.getMapCell(new Coordinate_2D(2,3));
        plan.addMove(new Move(testInstance.agents.get(0), 1, cell, cell));
        plans.put(testInstance.agents.get(0), plan);
        Solution expected = new Solution(plans);

        assertEquals(s, expected);
    }
}