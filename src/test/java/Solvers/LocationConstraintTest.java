package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationConstraintTest {

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

    @Test
    void accepts() {
        I_Map map1 = MapFactory.newSimple4Connected2D_GraphMap(map_2D_circle);
        I_Coordinate coor13 = new Coordinate_2D(1,3);
        I_Coordinate coor14 = new Coordinate_2D(1,4);
        I_Coordinate coor24 = new Coordinate_2D(2,4);
        I_Coordinate coor34 = new Coordinate_2D(3,4);
        Agent agent1 = new Agent(0, coor13, coor14);
        Agent agent2 = new Agent(0, coor24, coor24);

        // this move is just to illustrate why the constraint might exist, it isn't actually used
        Move move1 = new Move(agent1, 1, map1.getMapCell(coor13), map1.getMapCell(coor14));
        Move moveConflicts = new Move(agent2, 1, map1.getMapCell(coor24), map1.getMapCell(coor14));
        Move moveDoesntConflict = new Move(agent2, 1, map1.getMapCell(coor24), map1.getMapCell(coor34));

        LocationConstraint constraintHoldsSameAgent = new LocationConstraint(agent2, 1, map1.getMapCell(coor14));
        LocationConstraint constraintHoldsAllAgents = new LocationConstraint(null, 1, map1.getMapCell(coor14));

        LocationConstraint constraintDoesntHoldDifferentAgent = new LocationConstraint(agent1, 1, map1.getMapCell(coor14));
        LocationConstraint constraintDoesntHoldDifferentTime = new LocationConstraint(agent2, 2, map1.getMapCell(coor14));
        LocationConstraint constraintDoesntHoldDifferentlocation = new LocationConstraint(agent2, 1, map1.getMapCell(coor13));
        LocationConstraint constraintDoesntHoldPrevlocation = new LocationConstraint(agent2, 1, map1.getMapCell(coor24));

        /*  =should accept=  */
        /*  =  =because constraint doesn't hold=  */
        assertTrue(constraintDoesntHoldDifferentAgent.accepts(moveConflicts));
        assertTrue(constraintDoesntHoldDifferentTime.accepts(moveConflicts));
        assertTrue(constraintDoesntHoldDifferentlocation.accepts(moveConflicts));
        assertTrue(constraintDoesntHoldPrevlocation.accepts(moveConflicts));
        /*  =  =because move doesn't violate the constraint=  */
        assertTrue(constraintHoldsSameAgent.accepts(moveDoesntConflict));
        assertTrue(constraintHoldsAllAgents.accepts(moveDoesntConflict));

        /*  =should reject (return false)=  */
        assertFalse(constraintHoldsSameAgent.accepts(moveConflicts));
        assertFalse(constraintHoldsAllAgents.accepts(moveConflicts));

    }

}