package Solvers.ConstraintsAndConflicts;

import Solvers.SingleAgentPlan;

public interface I_ConflictAvoidanceTable {

    public A_Conflict selectConflict();

    public void add(SingleAgentPlan singleAgentPlan);

}
