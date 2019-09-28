package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SingleAgentPlanTest {

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
    private I_Map map1 = MapFactory.newSimple4Connected2D_GraphMap(map_2D_circle);
    private I_Coordinate coor12 = new Coordinate_2D(1,2);
    private I_Coordinate coor13 = new Coordinate_2D(1,3);
    private I_Coordinate coor14 = new Coordinate_2D(1,4);
    private I_Coordinate coor22 = new Coordinate_2D(2,2);
    private I_Coordinate coor24 = new Coordinate_2D(2,4);
    private I_Coordinate coor32 = new Coordinate_2D(3,2);
    private I_Coordinate coor33 = new Coordinate_2D(3,3);
    private I_Coordinate coor34 = new Coordinate_2D(3,4);
    private I_MapCell cell12 = map1.getMapCell(coor12);
    private I_MapCell cell13 = map1.getMapCell(coor13);
    private I_MapCell cell14 = map1.getMapCell(coor14);
    private I_MapCell cell22 = map1.getMapCell(coor22);
    private I_MapCell cell24 = map1.getMapCell(coor24);
    private I_MapCell cell32 = map1.getMapCell(coor32);
    private I_MapCell cell33 = map1.getMapCell(coor33);
    private I_MapCell cell34 = map1.getMapCell(coor34);
    private Agent agent1 = new Agent(0, coor13, coor14);
    private Agent agent2 = new Agent(1, coor24, coor24);


    /*  =valid inputs=  */
    //note that validity of move from one location to the next (neighbors or not) is not checked by SingleAgentPlan
    private Move move1agent1 = new Move(agent1, 1, cell13, cell14);
    private Move move2agent1 = new Move(agent1, 2, cell14, cell24);
    private Move move3agent1 = new Move(agent1, 3, cell24, cell14);
    private Move move1agent2 = new Move(agent2, 1, cell24, cell24);

    private Move move4agent1 = new Move(agent1, 4, cell14, cell13);

    /*  =invalid inputs=  */
    private Move move4agent1BadTime = new Move(agent1, 1, cell14, cell24);
    private Move move4agent1BadAgent = new Move(agent2, 4, cell14, cell24);

    /*  =plans=  */
    private SingleAgentPlan emptyPlanAgent1;
    private SingleAgentPlan emptyPlanAgent2;
    private SingleAgentPlan existingPlanAgent1;
    private SingleAgentPlan existingPlanAgent2;

    @BeforeEach
    void setUp() {
        /*  =init plans=  */
        emptyPlanAgent1 = new SingleAgentPlan(agent1);
        emptyPlanAgent2 = new SingleAgentPlan(agent2);

        List<Move> agent1Moves123 = new ArrayList<>();
        agent1Moves123.add(move1agent1);
        agent1Moves123.add(move2agent1);
        agent1Moves123.add(move3agent1);
        existingPlanAgent1 = new SingleAgentPlan(agent1, agent1Moves123);

        List<Move> agent2Moves1 = new ArrayList<>();
        existingPlanAgent2 = new SingleAgentPlan(agent2, agent2Moves1);
    }

    @Test
    void addMove() {
        /*  =shouldn't throw=  */
        assertDoesNotThrow(() -> emptyPlanAgent1.addMove(move1agent1));
        setUp();
        assertDoesNotThrow(() -> emptyPlanAgent1.addMove(move4agent1)); //can start at any time
        assertDoesNotThrow(() -> existingPlanAgent1.addMove(move4agent1));

        /*  =should throw=  */
        setUp();
        assertThrows(IllegalArgumentException.class,
                ()-> emptyPlanAgent1.addMove(move1agent2)); //bad agent
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMove(move4agent1BadAgent));
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMove(move4agent1BadTime));
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMove(move3agent1)); //bad time (duplicate move)
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMove(null)); //null
    }

    @Test
    void addMoves() {
        List<Move> a1moves123 = Arrays.asList(move1agent1, move2agent1, move3agent1);
        Move move5agent1 = new Move(agent1, 5, cell13, cell13);
        List<Move> a1moves45 = Arrays.asList(move4agent1, move5agent1);

        /*  =shouldn't throw=  */
        assertDoesNotThrow(() -> emptyPlanAgent1.addMoves(a1moves123));
        setUp();
        assertDoesNotThrow(() -> emptyPlanAgent1.addMoves(new ArrayList<>()));
        assertDoesNotThrow(() -> existingPlanAgent1.addMoves(a1moves45));
        assertDoesNotThrow(() -> existingPlanAgent1.addMoves(new ArrayList<>()));

        /*  =should throw=  */
        setUp();
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMoves(a1moves123)); //bad times
        assertThrows(IllegalArgumentException.class,
                ()-> emptyPlanAgent1.addMoves(Arrays.asList(move1agent2))); //bad agent
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMoves(Arrays.asList(new Move(agent2, 5, cell14, cell14)))); //bad agent
        assertThrows(IllegalArgumentException.class,
                ()-> emptyPlanAgent1.addMoves(Arrays.asList(
                        move1agent1, new Move(agent2, 2, cell14,cell14), move3agent1))); //bad agent middle
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMoves(Arrays.asList(
                        move4agent1, new Move(agent2, 5, cell14,cell14),
                        new Move(agent1, 6, cell14, cell14)))); //bad agent middle
        assertThrows(IllegalArgumentException.class,
                ()-> emptyPlanAgent1.addMoves(Arrays.asList(move1agent1, move3agent1, move3agent1))); //bad time middle
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMoves(Arrays.asList(move5agent1, move5agent1))); //bad time middle
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.addMoves(null)); //null
    }

    @Test
    void setMoves() {
        List<Move> a1moves123 = Arrays.asList(move1agent1, move2agent1, move3agent1);
        Move move5agent1 = new Move(agent1, 5, cell13, cell13);
        Move move4agent1 = new Move(agent1, 4, cell13, cell14);
        List<Move> a1moves45 = Arrays.asList(move4agent1, move5agent1);

        /*  =shouldn't throw=  */
        assertDoesNotThrow(() -> emptyPlanAgent1.setMoves(a1moves123));
        setUp();
        assertDoesNotThrow(() -> emptyPlanAgent1.setMoves(new ArrayList<>()));
        assertDoesNotThrow(() -> existingPlanAgent1.setMoves(a1moves45));
        setUp();
        assertDoesNotThrow(() -> existingPlanAgent1.setMoves(new ArrayList<>()));

        /*  =should throw=  */
        setUp();
        assertThrows(IllegalArgumentException.class,
                ()-> emptyPlanAgent1.setMoves(Arrays.asList(move1agent2))); //bad agent
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.setMoves(Arrays.asList(new Move(agent2, 5, cell14, cell14)))); //bad agent
        assertThrows(IllegalArgumentException.class,
                ()-> emptyPlanAgent1.setMoves(Arrays.asList(
                        move1agent1, new Move(agent2, 2, cell14,cell14), move3agent1))); //bad agent middle
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.setMoves(Arrays.asList(
                        move4agent1, new Move(agent2, 5, cell14,cell14),
                        new Move(agent1, 6, cell14, cell14)))); //bad agent middle
        assertThrows(IllegalArgumentException.class,
                ()-> emptyPlanAgent1.setMoves(Arrays.asList(move1agent1, move3agent1, move3agent1))); //bad time middle
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.setMoves(Arrays.asList(move5agent1, move4agent1, move5agent1))); //bad time middle
        assertThrows(IllegalArgumentException.class,
                ()-> existingPlanAgent1.setMoves(null)); //null
    }

    @Test
    void getStartTime() {
        /*  =as initiated=  */
        assertEquals(-1, emptyPlanAgent1.getStartTime());
        assertEquals(0, existingPlanAgent1.getStartTime());
        SingleAgentPlan planStartsAt3 = new SingleAgentPlan(agent1, Arrays.asList(new Move(agent1, 4, cell13, cell12)));
        assertEquals(3, planStartsAt3.getStartTime());

        /*  =when modified=  */
        emptyPlanAgent1.addMove(move2agent1);
        existingPlanAgent1.addMove(move4agent1);
        assertEquals(1, emptyPlanAgent1.getStartTime());
        assertEquals(0, existingPlanAgent1.getStartTime());
    }

    @Test
    void getEndTime() {
        /*  =as initiated=  */
        assertEquals(-1, emptyPlanAgent1.getEndTime());
        assertEquals(3, existingPlanAgent1.getEndTime());
        SingleAgentPlan planStartsAt3 = new SingleAgentPlan(agent1, Arrays.asList(new Move(agent1, 4, cell13, cell12)));
        assertEquals(4, planStartsAt3.getEndTime());

        /*  =when modified=  */
        emptyPlanAgent1.addMove(move2agent1);
        existingPlanAgent1.addMove(move4agent1);
        assertEquals(2, emptyPlanAgent1.getEndTime());
        assertEquals(4, existingPlanAgent1.getEndTime());
    }

    @Test
    void getElapsedTime() {
        /*  =as initiated=  */
        assertEquals(-1, emptyPlanAgent1.getTotalTime());
        assertEquals(3, existingPlanAgent1.getTotalTime());
        SingleAgentPlan planStartsAt3 = new SingleAgentPlan(agent1, Arrays.asList(new Move(agent1, 4, cell13, cell12)));
        assertEquals(1, planStartsAt3.getTotalTime());

        /*  =when modified=  */
        emptyPlanAgent1.addMove(move2agent1);
        assertEquals(1, emptyPlanAgent1.getTotalTime());
        emptyPlanAgent1.addMove(move3agent1);
        assertEquals(2, emptyPlanAgent1.getTotalTime());

        existingPlanAgent1.addMove(move4agent1);
        assertEquals(4, existingPlanAgent1.getTotalTime());
    }

    @Test
    void testToString(){
        System.out.println(existingPlanAgent1.toString());
        System.out.println(existingPlanAgent2.toString());

    }
}