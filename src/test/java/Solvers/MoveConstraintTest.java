package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveConstraintTest {

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
        // doesnt check things that are checked in LocationConstraintTest
        I_Map map1 = MapFactory.newSimple4Connected2D_GraphMap(map_2D_circle);
        I_Coordinate coor13 = new Coordinate_2D(1,3);
        I_Coordinate coor14 = new Coordinate_2D(1,4);
        I_Coordinate coor24 = new Coordinate_2D(2,4);
        I_Coordinate coor12 = new Coordinate_2D(1,2);
        Agent agent1 = new Agent(0, coor13, coor14);
        Agent agent2 = new Agent(0, coor24, coor24);

        // this move is just to illustrate why the constraint might exist, it isn't actually used
        Move move1 = new Move(agent1, 1, map1.getMapCell(coor13), map1.getMapCell(coor14));

        Move moveConflicts = new Move(agent2, 1, map1.getMapCell(coor14), map1.getMapCell(coor13));
        Move moveDoesntConflictOnMoveConstraint = new Move(agent2, 1, map1.getMapCell(coor12), map1.getMapCell(coor13));

        LocationConstraint constraintHoldsSameAgent = new MoveConstraint(agent2, 1, map1.getMapCell(coor14), map1.getMapCell(coor13));
        LocationConstraint constraintHoldsAllAgents = new MoveConstraint(null, 1, map1.getMapCell(coor14), map1.getMapCell(coor13));

        LocationConstraint constraintDoesntHoldDifferentPrevlocation = new MoveConstraint(agent2, 1, map1.getMapCell(coor12), map1.getMapCell(coor13));

        /*  =should accept=  */
        /*  =  =because constraint doesn't hold=  */
        assertTrue(constraintDoesntHoldDifferentPrevlocation.accepts(moveConflicts));
        /*  =  =because move doesn't violate the constraint=  */
        assertTrue(constraintHoldsSameAgent.accepts(moveDoesntConflictOnMoveConstraint));
        assertTrue(constraintHoldsAllAgents.accepts(moveDoesntConflictOnMoveConstraint));

        /*  =should reject (return false)=  */
        assertFalse(constraintHoldsSameAgent.accepts(moveConflicts));
        assertFalse(constraintHoldsAllAgents.accepts(moveConflicts));

    }

    @Test
    void rejects() {
    }
}