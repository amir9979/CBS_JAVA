package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

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

}
