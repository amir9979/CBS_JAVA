package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;
import Solvers.Move;

import java.util.Objects;

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

    /**
     * @return an array of constraints, each of which could prevent this conflict
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof A_Conflict)) return false;
        A_Conflict conflict = (A_Conflict) o;
        return time == conflict.time &&
                (( Objects.equals(agent1, conflict.agent1) && Objects.equals(agent2, conflict.agent2)) ||
                 ( Objects.equals(agent1, conflict.agent2) && Objects.equals(agent2, conflict.agent1))  ) &&
                  Objects.equals(location, conflict.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash( agent1 ) * Objects.hash( agent2 ) * Objects.hash( time, location );
    }
}
