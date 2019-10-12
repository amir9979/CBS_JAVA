package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;
import Solvers.Move;

public abstract class A_Conflict {
    public final Agent agent1;
    public final Agent agent2;
    public final int time;
    public final I_MapCell location;

    public A_Conflict(Agent agent1, Agent agent2, int time, I_MapCell location) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.time = time;
        this.location = location;
    }

    public abstract Constraint[] getPreventingConstraints();

//    public static boolean hasConflicts(Solution solution) {
//        return false; //imp
//    }
//
//    public static A_Conflict firstConflict(Solution solution){
//        return null; //imp
//    }
//
//    public static A_Conflict[] allConflicts(Solution solution){
//        return null; //imp
//    }
//
//    public static boolean haveConflicts(SingleAgentPlan plan1, SingleAgentPlan plan2){
//        return true; //imp
//    }
//
//    public static A_Conflict firstConflict(SingleAgentPlan plan1, SingleAgentPlan plan2){
//        return null; //imp
//    }
//
//    public static A_Conflict[] allConflicts(SingleAgentPlan plan1, SingleAgentPlan plan2){
//        return null; //imp
//    }

    /**
     * Checks that both moves have the same time.
     * @param move1 @NotNull
     * @param move2 @NotNull
     * @return true if these moves have a vertex conflict or a swapping conflict.
     */
    public static boolean haveConflicts(Move move1, Move move2){
        if(move1 == null || move2 == null){throw new IllegalArgumentException("can't compare null moves");}

        return move1.timeNow == move2.timeNow
                && (SwappingConflict.haveConflicts(move1, move2) || VertexConflict.haveConflicts(move1, move2));
    }

}
