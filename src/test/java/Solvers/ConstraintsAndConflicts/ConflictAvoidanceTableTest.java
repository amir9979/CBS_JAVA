package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.*;
import Solvers.Move;
import Solvers.SingleAgentPlan;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ConflictAvoidanceTableTest {

    private final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    private final Enum_MapCellType w = Enum_MapCellType.WALL;
    private Enum_MapCellType[][] map_2D_H = {
            { e, w, w, e},
            { e, e, e, e},
            { e, w, w, e},
    };
    private I_Map mapH = MapFactory.newSimple4Connected2D_GraphMap(map_2D_H);





    private boolean equalsAllConflicts(Set<A_Conflict> expectedConflicts, Set<A_Conflict> actualConflicts){

        if( actualConflicts.size() != expectedConflicts.size() ){
            return false;
        }
        for (A_Conflict conflict: expectedConflicts){
            if (! actualConflicts.contains(conflict)){
                return false;
            }
        }
        return true;
    }


    private boolean equalsAllAgents(Set<Agent> expectedAgents, Set<Agent> actualAgents){

        if( expectedAgents.size() != actualAgents.size() ){
            return false;
        }

            for (Agent agent: expectedAgents){
            if (! actualAgents.contains(agent)){
                return false;
            }
        }
        return true;
    }


    private boolean equalsTimeLocations(HashMap<ConflictAvoidanceTable.TimeLocation,HashSet<Agent>> expectedConflicts, HashMap<ConflictAvoidanceTable.TimeLocation,HashSet<Agent>> actualConflicts){

        if( actualConflicts.size() != expectedConflicts.size() ){
            return false;
        }
        for (Map.Entry<ConflictAvoidanceTable.TimeLocation,HashSet<Agent>> timeLocation_agents: expectedConflicts.entrySet()){

            ConflictAvoidanceTable.TimeLocation timeLocation = timeLocation_agents.getKey();
            HashSet<Agent> expectedAgents = expectedConflicts.get(timeLocation);
            HashSet<Agent> actualAgents = actualConflicts.get(timeLocation);
            if (! this.equalsAllAgents(expectedAgents,actualAgents)){
                return false;
            }
        }
        return true;
    }




    @Test
    public void swappingConflict2CellMap(){

        ConflictAvoidanceTable conflictAvoidanceTable = new ConflictAvoidanceTable(new MinTimeConflictSelectionStrategy());

        Enum_MapCellType[][] twoCellMap = new Enum_MapCellType[][]{{e,e}};
        I_Map mapTwoCells = MapFactory.newSimple4Connected2D_GraphMap(twoCellMap);

        /*  = Add a1 Plan =
            { S1 , G1 }
            S = Start
            G = Goal
        */
        Agent a1 = new Agent(1,new Coordinate_2D(0,0),new Coordinate_2D(0,1));
        SingleAgentPlan a1_plan;
        ArrayList<Move> a1_moves = new ArrayList<>();
        a1_moves.add(new Move(a1,1,mapTwoCells.getMapCell(new Coordinate_2D(0,0)),mapTwoCells.getMapCell(new Coordinate_2D(0,1))));

        a1_plan = new SingleAgentPlan(a1,a1_moves);
        conflictAvoidanceTable.add(a1_plan);




        /*  = Add a2 Plan =
            { G2 , S2 }
            S = Start
            G = Goal
        */
        Agent a2 = new Agent(2,new Coordinate_2D(0,1),new Coordinate_2D(0,0));
        SingleAgentPlan a2_plan;
        ArrayList<Move> a2_moves = new ArrayList<>();
        a2_moves.add(new Move(a2,1,mapTwoCells.getMapCell(new Coordinate_2D(0,1)),mapTwoCells.getMapCell(new Coordinate_2D(0,0))));


        a2_plan = new SingleAgentPlan(a2,a2_moves);
        conflictAvoidanceTable.add(a2_plan);


        /*      == Expected conflicts ==     */

        SwappingConflict expectedConflict_a1 = new SwappingConflict(a1,a2,1,mapTwoCells.getMapCell(new Coordinate_2D(0,1)),mapTwoCells.getMapCell(new Coordinate_2D(0,0)));
        SwappingConflict expectedConflict_a2 = new SwappingConflict(a2,a1,1,mapTwoCells.getMapCell(new Coordinate_2D(0,0)),mapTwoCells.getMapCell(new Coordinate_2D(0,1)));

        HashSet<A_Conflict> expectedSet = new HashSet<>();
        expectedSet.add(expectedConflict_a1);
        expectedSet.add(expectedConflict_a2);


        /*      = Test actual values =  */
        Assert.assertTrue(equalsAllConflicts(expectedSet, conflictAvoidanceTable.allConflicts));


    }



    @Test
    public void TwoAgentsWith4VertexConflicts_graphH() {

        ConflictAvoidanceTable conflictAvoidanceTable = new ConflictAvoidanceTable(new MinTimeConflictSelectionStrategy());


        /*  = Add a1 Plan =
            { S1, WW, WW, G1},
            { T1, T2, T3, T4},
            { EE, WW, WW, EE},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a1 = new Agent(1,new Coordinate_2D(0,0),new Coordinate_2D(0,3));
        SingleAgentPlan a1_plan;
        ArrayList<Move> a1_moves = new ArrayList<>();

        a1_moves.add(new Move(a1,1, mapH.getMapCell(new Coordinate_2D(0,0)),mapH.getMapCell(new Coordinate_2D(1,0))));
        a1_moves.add(new Move(a1,2, mapH.getMapCell(new Coordinate_2D(1,0)),mapH.getMapCell(new Coordinate_2D(1,1))));
        a1_moves.add(new Move(a1,3, mapH.getMapCell(new Coordinate_2D(1,1)),mapH.getMapCell(new Coordinate_2D(1,2))));
        a1_moves.add(new Move(a1,4, mapH.getMapCell(new Coordinate_2D(1,2)),mapH.getMapCell(new Coordinate_2D(1,3))));
        a1_moves.add(new Move(a1,5, mapH.getMapCell(new Coordinate_2D(1,3)),mapH.getMapCell(new Coordinate_2D(0,3))));

        a1_plan = new SingleAgentPlan(a1,a1_moves);
        conflictAvoidanceTable.add(a1_plan);

        /*  = Add a2 Plan =
            { EE, WW, WW, EE},
            { T1, T2, T3, T4},
            { S2, WW, WW, G2},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a2 = new Agent(2,new Coordinate_2D(2,0),new Coordinate_2D(2,3));
        SingleAgentPlan a2_plan;
        ArrayList<Move> a2_moves = new ArrayList<>();

        a2_moves.add(new Move(a2,1, mapH.getMapCell(new Coordinate_2D(2,0)), mapH.getMapCell(new Coordinate_2D(1,0))));
        a2_moves.add(new Move(a2,2, mapH.getMapCell(new Coordinate_2D(1,0)), mapH.getMapCell(new Coordinate_2D(1,1))));
        a2_moves.add(new Move(a2,3, mapH.getMapCell(new Coordinate_2D(1,1)), mapH.getMapCell(new Coordinate_2D(1,2))));
        a2_moves.add(new Move(a2,4, mapH.getMapCell(new Coordinate_2D(1,2)), mapH.getMapCell(new Coordinate_2D(1,3))));
        a2_moves.add(new Move(a2,5, mapH.getMapCell(new Coordinate_2D(1,3)), mapH.getMapCell(new Coordinate_2D(2,3))));

        a2_plan = new SingleAgentPlan(a2,a2_moves);
        conflictAvoidanceTable.add(a2_plan);


        System.out.println("TwoAgentsWith4VertexConflicts_graphH: Done - Initialized two plans");


        /*      = Copy constructor =      */
        ConflictAvoidanceTable copiedTable = new ConflictAvoidanceTable(conflictAvoidanceTable);
        Assert.assertTrue(this.equalsAllConflicts(conflictAvoidanceTable.allConflicts,copiedTable.allConflicts));
        Assert.assertTrue(this.equalsTimeLocations(conflictAvoidanceTable.timeLocation_Agents,copiedTable.timeLocation_Agents));
        System.out.println("TwoAgentsWith4VertexConflicts_graphH: Done - Copy Constructor");



        /*      = Expected values =     */

        /*      == Expected locations ==     */

        HashMap<ConflictAvoidanceTable.TimeLocation,HashSet<Agent>> expected_timeLocationAgents = new HashMap<>();
        // Agent 1
        ConflictAvoidanceTable.TimeLocation time0_a1 = new ConflictAvoidanceTable.TimeLocation(0, mapH.getMapCell(new Coordinate_2D(0,0)));
        expected_timeLocationAgents.computeIfAbsent(time0_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time1_a1 = new ConflictAvoidanceTable.TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time1_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time2_a1 = new ConflictAvoidanceTable.TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time2_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time3_a1 = new ConflictAvoidanceTable.TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time3_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time4_a1 = new ConflictAvoidanceTable.TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time4_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time5_a1 = new ConflictAvoidanceTable.TimeLocation(5, mapH.getMapCell(new Coordinate_2D(0,3)));
        expected_timeLocationAgents.computeIfAbsent(time5_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a1).add(a1);

        // Agent 2
        ConflictAvoidanceTable.TimeLocation time0_a2 = new ConflictAvoidanceTable.TimeLocation(0, mapH.getMapCell(new Coordinate_2D(2,0)));
        expected_timeLocationAgents.computeIfAbsent(time0_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time1_a2 = new ConflictAvoidanceTable.TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time1_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time2_a2 = new ConflictAvoidanceTable.TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time2_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time3_a2 = new ConflictAvoidanceTable.TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time3_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time4_a2 = new ConflictAvoidanceTable.TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time4_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time5_a2 = new ConflictAvoidanceTable.TimeLocation(5, mapH.getMapCell(new Coordinate_2D(2,3)));
        expected_timeLocationAgents.computeIfAbsent(time5_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a2).add(a2);


        /*      == Expected conflicts ==     */

        VertexConflict expectedConflict_time1 = new VertexConflict(a1,a2,1,mapH.getMapCell(new Coordinate_2D(1,0)));
        VertexConflict expectedConflict_time2 = new VertexConflict(a1,a2,2,mapH.getMapCell(new Coordinate_2D(1,1)));
        VertexConflict expectedConflict_time3 = new VertexConflict(a1,a2,3,mapH.getMapCell(new Coordinate_2D(1,2)));
        VertexConflict expectedConflict_time4 = new VertexConflict(a1,a2,4,mapH.getMapCell(new Coordinate_2D(1,3)));

        HashSet<A_Conflict> expectedSet = new HashSet<>();
        expectedSet.add(expectedConflict_time1);
        expectedSet.add(expectedConflict_time2);
        expectedSet.add(expectedConflict_time3);
        expectedSet.add(expectedConflict_time4);


        /*  = Test actual values =  */

        Assert.assertTrue(equalsAllConflicts(expectedSet, copiedTable.allConflicts));
        Assert.assertTrue(equalsTimeLocations(expected_timeLocationAgents,copiedTable.timeLocation_Agents));




        /*      = Test Select conflict =     */
        A_Conflict actualConflict_time1 = copiedTable.selectConflict();
        Assert.assertEquals(expectedConflict_time1,actualConflict_time1);



        /*    = Agent 1 new Plan =    */
        // Waits at start  position for t = 1
        SingleAgentPlan a1_newPlan;
        ArrayList<Move> a1_newMoves = new ArrayList<>();

        a1_newMoves.add(new Move(a1,1, mapH.getMapCell(new Coordinate_2D(0,0)),mapH.getMapCell(new Coordinate_2D(0,0))));
        a1_newMoves.add(new Move(a1,2, mapH.getMapCell(new Coordinate_2D(0,0)),mapH.getMapCell(new Coordinate_2D(1,0))));
        a1_newMoves.add(new Move(a1,3, mapH.getMapCell(new Coordinate_2D(1,0)),mapH.getMapCell(new Coordinate_2D(1,1))));
        a1_newMoves.add(new Move(a1,4, mapH.getMapCell(new Coordinate_2D(1,1)),mapH.getMapCell(new Coordinate_2D(1,2))));
        a1_newMoves.add(new Move(a1,5, mapH.getMapCell(new Coordinate_2D(1,2)),mapH.getMapCell(new Coordinate_2D(1,3))));
        a1_newMoves.add(new Move(a1,6, mapH.getMapCell(new Coordinate_2D(1,3)),mapH.getMapCell(new Coordinate_2D(0,3))));

        a1_newPlan = new SingleAgentPlan(a1,a1_newMoves);
        copiedTable.add(a1_newPlan);


        System.out.println("TwoAgentsWith4VertexConflicts_graphH: Done - Add agent1 new plan");


        /*      = Expected values =     */
        expectedSet = new HashSet<>();


        /*      = Test actual values =  */
        Assert.assertTrue(equalsAllConflicts(expectedSet, copiedTable.allConflicts));


    }





    @Test
    public void TwoAgentsWith1SwappingConflict_graphH() {

        ConflictAvoidanceTable conflictAvoidanceTable = new ConflictAvoidanceTable(new MinTimeConflictSelectionStrategy());


        /*  = Add a1 Plan =
            { S1, WW, WW, G1},
            { T1, T2, T3, T4},
            { EE, WW, WW, EE},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a1 = new Agent(1,new Coordinate_2D(0,0),new Coordinate_2D(0,3));
        SingleAgentPlan a1_plan;
        ArrayList<Move> a1_moves = new ArrayList<>();

        a1_moves.add(new Move(a1,1, mapH.getMapCell(new Coordinate_2D(0,0)),mapH.getMapCell(new Coordinate_2D(1,0))));
        a1_moves.add(new Move(a1,2, mapH.getMapCell(new Coordinate_2D(1,0)),mapH.getMapCell(new Coordinate_2D(1,1))));
        a1_moves.add(new Move(a1,3, mapH.getMapCell(new Coordinate_2D(1,1)),mapH.getMapCell(new Coordinate_2D(1,2))));
        a1_moves.add(new Move(a1,4, mapH.getMapCell(new Coordinate_2D(1,2)),mapH.getMapCell(new Coordinate_2D(1,3))));
        a1_moves.add(new Move(a1,5, mapH.getMapCell(new Coordinate_2D(1,3)),mapH.getMapCell(new Coordinate_2D(0,3))));

        a1_plan = new SingleAgentPlan(a1,a1_moves);
        conflictAvoidanceTable.add(a1_plan);


        /*  = Add a2 Plan =
            { EE, WW, WW, EE},
            { T4, T3, T2, T1},
            { G1, WW, WW, S2},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a2 = new Agent(2,new Coordinate_2D(2,3),new Coordinate_2D(2,0));
        SingleAgentPlan a2_plan;
        ArrayList<Move> a2_moves = new ArrayList<>();

        a2_moves.add(new Move(a2,1, mapH.getMapCell(new Coordinate_2D(2,3)), mapH.getMapCell(new Coordinate_2D(1,3))));
        a2_moves.add(new Move(a2,2, mapH.getMapCell(new Coordinate_2D(1,3)), mapH.getMapCell(new Coordinate_2D(1,2))));
        a2_moves.add(new Move(a2,3, mapH.getMapCell(new Coordinate_2D(1,2)), mapH.getMapCell(new Coordinate_2D(1,1))));
        a2_moves.add(new Move(a2,4, mapH.getMapCell(new Coordinate_2D(1,1)), mapH.getMapCell(new Coordinate_2D(1,0))));
        a2_moves.add(new Move(a2,5, mapH.getMapCell(new Coordinate_2D(1,0)), mapH.getMapCell(new Coordinate_2D(2,0))));

        a2_plan = new SingleAgentPlan(a2,a2_moves);
        conflictAvoidanceTable.add(a2_plan);


        System.out.println("TwoAgentsWith1SwappingConflict_graphH: Done - Initialized two plans");


        /*      = Copy constructor =      */
        ConflictAvoidanceTable copiedTable = new ConflictAvoidanceTable(conflictAvoidanceTable);
        Assert.assertTrue(this.equalsAllConflicts(conflictAvoidanceTable.allConflicts,copiedTable.allConflicts));
        Assert.assertTrue(this.equalsTimeLocations(conflictAvoidanceTable.timeLocation_Agents,copiedTable.timeLocation_Agents));
        System.out.println("TwoAgentsWith1SwappingConflict_graphH: Done - Copy Constructor");



        /*      = Expected values =     */

        /*      == Expected locations ==     */

        HashMap<ConflictAvoidanceTable.TimeLocation,HashSet<Agent>> expected_timeLocationAgents = new HashMap<>();
        // Agent 1
        ConflictAvoidanceTable.TimeLocation time0_a1 = new ConflictAvoidanceTable.TimeLocation(0, mapH.getMapCell(new Coordinate_2D(0,0)));
        expected_timeLocationAgents.computeIfAbsent(time0_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time1_a1 = new ConflictAvoidanceTable.TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time1_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time2_a1 = new ConflictAvoidanceTable.TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time2_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time3_a1 = new ConflictAvoidanceTable.TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time3_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time4_a1 = new ConflictAvoidanceTable.TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time4_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a1).add(a1);
        ConflictAvoidanceTable.TimeLocation time5_a1 = new ConflictAvoidanceTable.TimeLocation(5, mapH.getMapCell(new Coordinate_2D(0,3)));
        expected_timeLocationAgents.computeIfAbsent(time5_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a1).add(a1);

        // Agent 2
        ConflictAvoidanceTable.TimeLocation time0_a2 = new ConflictAvoidanceTable.TimeLocation(0, mapH.getMapCell(new Coordinate_2D(2,3)));
        expected_timeLocationAgents.computeIfAbsent(time0_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time1_a2 = new ConflictAvoidanceTable.TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time1_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time2_a2 = new ConflictAvoidanceTable.TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time2_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time3_a2 = new ConflictAvoidanceTable.TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time3_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time4_a2 = new ConflictAvoidanceTable.TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time4_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a2).add(a2);
        ConflictAvoidanceTable.TimeLocation time5_a2 = new ConflictAvoidanceTable.TimeLocation(5, mapH.getMapCell(new Coordinate_2D(2,0)));
        expected_timeLocationAgents.computeIfAbsent(time5_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a2).add(a2);


        /*      == Expected conflicts ==     */

        SwappingConflict expectedConflict_a1_time3 = new SwappingConflict(a1,a2,3,mapH.getMapCell(new Coordinate_2D(1,2)),mapH.getMapCell(new Coordinate_2D(1,1)));
        SwappingConflict expectedConflict_a2_time3 = new SwappingConflict(a2,a1,3,mapH.getMapCell(new Coordinate_2D(1,1)),mapH.getMapCell(new Coordinate_2D(1,2)));

        HashSet<A_Conflict> expectedSet = new HashSet<>();
        expectedSet.add(expectedConflict_a1_time3);
        expectedSet.add(expectedConflict_a2_time3);


        /*  = Test actual values =  */

        Assert.assertTrue(equalsAllConflicts(expectedSet, copiedTable.allConflicts));
        Assert.assertTrue(equalsTimeLocations(expected_timeLocationAgents,copiedTable.timeLocation_Agents));



        /*      = Test Select conflict =     */
        A_Conflict actualConflict_time3 = copiedTable.selectConflict();
        Assert.assertEquals(3, actualConflict_time3.time);



        /*    = Agent 1 new Plan =    */



        SingleAgentPlan a1_newPlan;
        ArrayList<Move> a1_newMoves = new ArrayList<>();

        a1_newMoves.add(new Move(a1,1, mapH.getMapCell(new Coordinate_2D(0,0)),mapH.getMapCell(new Coordinate_2D(0,0))));
        a1_newMoves.add(new Move(a1,2, mapH.getMapCell(new Coordinate_2D(0,0)),mapH.getMapCell(new Coordinate_2D(1,0))));
        a1_newMoves.add(new Move(a1,3, mapH.getMapCell(new Coordinate_2D(1,0)),mapH.getMapCell(new Coordinate_2D(1,1))));
        a1_newMoves.add(new Move(a1,4, mapH.getMapCell(new Coordinate_2D(1,1)),mapH.getMapCell(new Coordinate_2D(1,2))));
        a1_newMoves.add(new Move(a1,5, mapH.getMapCell(new Coordinate_2D(1,2)),mapH.getMapCell(new Coordinate_2D(1,3))));
        a1_newMoves.add(new Move(a1,6, mapH.getMapCell(new Coordinate_2D(1,3)),mapH.getMapCell(new Coordinate_2D(0,3))));

        a1_newPlan = new SingleAgentPlan(a1,a1_newMoves);
        copiedTable.add(a1_newPlan);


        System.out.println("TwoAgentsWith1SwappingConflict_graphH: Done - Add agent1 new plan");


        /*      = Expected values =     */
        expectedSet = new HashSet<>();
        VertexConflict expectedVertexConflict_time3 = new VertexConflict(a1,a2,3,mapH.getMapCell(new Coordinate_2D(1,1)));
        expectedSet.add(expectedVertexConflict_time3);


        /*      = Test actual values =  */
        Assert.assertTrue(equalsAllConflicts(expectedSet, copiedTable.allConflicts));


    }



}