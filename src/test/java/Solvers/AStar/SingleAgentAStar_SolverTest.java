package Solvers.AStar;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.MAPF_Instance;
import Instances.Maps.*;
import Solvers.*;
import Solvers.ConstraintsAndConflicts.Constraint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private Agent agent33to12 = new Agent(0, new Coordinate_2D(3,3), new Coordinate_2D(1, 2));
    private Agent agent12to33 = new Agent(0, new Coordinate_2D(1,2), new Coordinate_2D(3, 3));
    private Agent agent53to05 = new Agent(1, new Coordinate_2D(5,3), new Coordinate_2D(0, 5));

    private I_Coordinate coor12 = new Coordinate_2D(1,2);
    private I_Coordinate coor13 = new Coordinate_2D(1,3);
    private I_Coordinate coor14 = new Coordinate_2D(1,4);
    private I_Coordinate coor22 = new Coordinate_2D(2,2);
    private I_Coordinate coor24 = new Coordinate_2D(2,4);
    private I_Coordinate coor32 = new Coordinate_2D(3,2);
    private I_Coordinate coor33 = new Coordinate_2D(3,3);
    private I_Coordinate coor34 = new Coordinate_2D(3,4);
    private I_MapCell cell12 = mapCircle.getMapCell(coor12);
    private I_MapCell cell13 = mapCircle.getMapCell(coor13);
    private I_MapCell cell14 = mapCircle.getMapCell(coor14);
    private I_MapCell cell22 = mapCircle.getMapCell(coor22);
    private I_MapCell cell24 = mapCircle.getMapCell(coor24);
    private I_MapCell cell32 = mapCircle.getMapCell(coor32);
    private I_MapCell cell33 = mapCircle.getMapCell(coor33);
    private I_MapCell cell34 = mapCircle.getMapCell(coor34);

    InstanceBuilder_BGU builder = new InstanceBuilder_BGU();
    InstanceManager im = new InstanceManager(IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,"Instances"}),
            new InstanceBuilder_BGU(), new InstanceProperties(new int[]{6,6},0,1,"-"));

    private MAPF_Instance instanceEmpty = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent53to05});
    private MAPF_Instance instance1stepSolution = im.getNextInstance();
    private MAPF_Instance instanceCircle1 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent33to12});
    private MAPF_Instance instanceCircle2 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent12to33});

    I_Solver aStar = new SingleAgentAStar_Solver();

    @BeforeEach
    void setUp() {

    }

    @Test
    void oneMoveSolution() {
        MAPF_Instance testInstance = instance1stepSolution;
        Solution s = aStar.solve(testInstance, new RunParameters());

        Map<Agent, SingleAgentPlan> plans = new HashMap<>();
        SingleAgentPlan plan = new SingleAgentPlan(testInstance.agents.get(0));
        I_MapCell cell = testInstance.map.getMapCell(new Coordinate_2D(4,5));
        plan.addMove(new Move(testInstance.agents.get(0), 1, cell, cell));
        plans.put(testInstance.agents.get(0), plan);
        Solution expected = new Solution(plans);

        assertEquals(s, expected);
        assertTrue(s.isValidSolution());
    }

    @Test
    void circleOptimality1(){
        MAPF_Instance testInstance = instanceCircle1;
        Agent agent = testInstance.agents.get(0);

        Solution solved = aStar.solve(testInstance, new RunParameters());

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell33, cell33));
        plan.addMove(new Move(agent, 2, cell33, cell32));
        plan.addMove(new Move(agent, 3, cell32, cell22));
        plan.addMove(new Move(agent, 4, cell22, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        //includes a first move of staying put
        assertEquals(4, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);

    }

    @Test
    void circleOptimalityWaitingBecauseOfConstraint(){
        MAPF_Instance testInstance = instanceCircle1;
        Agent agent = testInstance.agents.get(0);

        //constraint
        Constraint vertexConstraint = new Constraint(null, 2, null, cell32);
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(vertexConstraint);
        RunParameters parameters = new RunParameters(constraints);

        Solution solved = aStar.solve(testInstance, parameters);

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell33, cell33));
        plan.addMove(new Move(agent, 2, cell33, cell33));
        plan.addMove(new Move(agent, 3, cell33, cell32));
        plan.addMove(new Move(agent, 4, cell32, cell22));
        plan.addMove(new Move(agent, 5, cell22, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        //includes a first move of staying put
        assertEquals(5, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);

    }

    @Test
    void circleOptimalityOtherDirectionBecauseOfConstraints(){
        MAPF_Instance testInstance = instanceCircle1;
        Agent agent = testInstance.agents.get(0);

        //constraint
        Constraint swappingConstraint1 = new Constraint(null, 2, cell33, cell32);
        Constraint swappingConstraint2 = new Constraint(null, 3, cell33, cell32);
        Constraint swappingConstraint3 = new Constraint(null, 4, cell33, cell32);
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(swappingConstraint1);
        constraints.add(swappingConstraint2);
        constraints.add(swappingConstraint3);
        RunParameters parameters = new RunParameters(constraints);

        Solution solved = aStar.solve(testInstance, parameters);

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell33, cell33));
        plan.addMove(new Move(agent, 2, cell33, cell34));
        plan.addMove(new Move(agent, 3, cell34, cell24));
        plan.addMove(new Move(agent, 4, cell24, cell14));
        plan.addMove(new Move(agent, 5, cell14, cell13));
        plan.addMove(new Move(agent, 6, cell13, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        //includes a first move of staying put
        assertEquals(6, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);

    }

    @Test
    void circleOptimalityNorthwestToSoutheast(){
        MAPF_Instance testInstance = instanceCircle2;
        Agent agent = testInstance.agents.get(0);

        Solution solved = aStar.solve(testInstance, new RunParameters());

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell12, cell12));
        plan.addMove(new Move(agent, 2, cell12, cell22));
        plan.addMove(new Move(agent, 3, cell22, cell32));
        plan.addMove(new Move(agent, 4, cell32, cell33));
        Solution expected = new Solution();
        expected.putPlan(plan);

        //includes a first move of staying put
        assertEquals(4, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);
    }
}