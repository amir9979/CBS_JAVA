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
import Solvers.ConstraintsAndConflicts.ConstraintSet;
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

    private Agent agent33to12 = new Agent(0, coor33, coor12);
    private Agent agent12to33 = new Agent(1, coor12, coor33);
    private Agent agent53to05 = new Agent(0, coor53, coor05);
    private Agent agent43to11 = new Agent(0, coor43, coor11);
    private Agent agent04to00 = new Agent(0, coor04, coor00);

    InstanceBuilder_BGU builder = new InstanceBuilder_BGU();
    InstanceManager im = new InstanceManager(IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,"Instances"}),
            new InstanceBuilder_BGU(), new InstanceProperties(new MapDimensions(new int[]{6,6}),0f,new int[]{1}));

    private MAPF_Instance instanceEmpty1 = new MAPF_Instance("instanceEmpty", mapEmpty, new Agent[]{agent53to05});
    private MAPF_Instance instanceEmpty2 = new MAPF_Instance("instanceEmpty", mapEmpty, new Agent[]{agent43to11});
    private MAPF_Instance instance1stepSolution = im.getNextInstance();
    private MAPF_Instance instanceCircle1 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent33to12});
    private MAPF_Instance instanceCircle2 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent12to33});
    private MAPF_Instance instanceUnsolvable = new MAPF_Instance("instanceUnsolvable", mapWithPocket, new Agent[]{agent04to00});

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
        plan.addMove(new Move(agent, 1, cell33, cell32));
        plan.addMove(new Move(agent, 2, cell32, cell22));
        plan.addMove(new Move(agent, 3, cell22, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        assertEquals(3, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);

    }

    @Test
    void circleOptimalityWaitingBecauseOfConstraint1(){
        MAPF_Instance testInstance = instanceCircle1;
        Agent agent = testInstance.agents.get(0);

        //constraint
        Constraint vertexConstraint = new Constraint(null, 1, null, cell32);
        ConstraintSet constraints = new ConstraintSet();
        constraints.add(vertexConstraint);
        RunParameters parameters = new RunParameters(constraints);

        Solution solved = aStar.solve(testInstance, parameters);

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell33, cell33));
        plan.addMove(new Move(agent, 2, cell33, cell32));
        plan.addMove(new Move(agent, 3, cell32, cell22));
        plan.addMove(new Move(agent, 4, cell22, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        assertEquals(4, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);

    }

    @Test
    void circleOptimalityWaitingBecauseOfConstraint2(){
        MAPF_Instance testInstance = instanceCircle1;
        Agent agent = testInstance.agents.get(0);

        //constraint
        Constraint vertexConstraint = new Constraint(agent, 1, null, cell32);
        ConstraintSet constraints = new ConstraintSet();
        constraints.add(vertexConstraint);
        RunParameters parameters = new RunParameters(constraints);

        Solution solved = aStar.solve(testInstance, parameters);

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell33, cell33));
        plan.addMove(new Move(agent, 2, cell33, cell32));
        plan.addMove(new Move(agent, 3, cell32, cell22));
        plan.addMove(new Move(agent, 4, cell22, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        assertEquals(4, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);
    }

    @Test
    void circleOptimalityWaitingBecauseOfConstraint3(){
        MAPF_Instance testInstance = instanceCircle1;
        Agent agent = testInstance.agents.get(0);

        //constraint
        Constraint swappingConstraint = new Constraint(agent, 1, cell33, cell32);
        ConstraintSet constraints = new ConstraintSet();
        constraints.add(swappingConstraint);
        RunParameters parameters = new RunParameters(constraints);

        Solution solved = aStar.solve(testInstance, parameters);

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell33, cell33));
        plan.addMove(new Move(agent, 2, cell33, cell32));
        plan.addMove(new Move(agent, 3, cell32, cell22));
        plan.addMove(new Move(agent, 4, cell22, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        assertEquals(4, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);
    }

    @Test
    void circleOptimalityOtherDirectionBecauseOfConstraints(){
        MAPF_Instance testInstance = instanceCircle1;
        Agent agent = testInstance.agents.get(0);

        //constraint
        Constraint swappingConstraint1 = new Constraint(null, 1, cell33, cell32);
        Constraint swappingConstraint2 = new Constraint(null, 2, cell33, cell32);
        Constraint swappingConstraint3 = new Constraint(null, 3, cell33, cell32);
        ConstraintSet constraints = new ConstraintSet();
        constraints.add(swappingConstraint1);
        constraints.add(swappingConstraint2);
        constraints.add(swappingConstraint3);
        RunParameters parameters = new RunParameters(constraints);

        Solution solved = aStar.solve(testInstance, parameters);

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell33, cell34));
        plan.addMove(new Move(agent, 2, cell34, cell24));
        plan.addMove(new Move(agent, 3, cell24, cell14));
        plan.addMove(new Move(agent, 4, cell14, cell13));
        plan.addMove(new Move(agent, 5, cell13, cell12));
        Solution expected = new Solution();
        expected.putPlan(plan);

        assertEquals(5, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);

    }

    @Test
    void circleOptimalityNorthwestToSoutheast(){
        MAPF_Instance testInstance = instanceCircle2;
        Agent agent = testInstance.agents.get(0);

        Solution solved = aStar.solve(testInstance, new RunParameters());

        SingleAgentPlan plan = new SingleAgentPlan(agent);
        plan.addMove(new Move(agent, 1, cell12, cell22));
        plan.addMove(new Move(agent, 2, cell22, cell32));
        plan.addMove(new Move(agent, 3, cell32, cell33));
        Solution expected = new Solution();
        expected.putPlan(plan);

        assertEquals(3, solved.getPlanFor(agent).size());
        assertEquals(expected, solved);
    }

    @Test
    void emptyOptimality(){
        MAPF_Instance testInstance1 = instanceEmpty1;
        Agent agent1 = testInstance1.agents.get(0);

        MAPF_Instance testInstance2 = instanceEmpty2;
        Agent agent2 = testInstance2.agents.get(0);

        Solution solved1 = aStar.solve(testInstance1, new RunParameters());
        Solution solved2 = aStar.solve(testInstance2, new RunParameters());

        assertEquals(7, solved1.getPlanFor(agent1).size());
        assertEquals(5, solved2.getPlanFor(agent2).size());
    }

    @Test
    void unsolvableShouldTimeout(){
        MAPF_Instance testInstance = instanceUnsolvable;

        // three second timeout
        RunParameters runParameters = new RunParameters(1000*3);
        Solution solved = aStar.solve(testInstance, runParameters);

        assertNull(solved);
    }
}