package Solvers.ConstraintsAndConflicts;

import Solvers.SingleAgentPlan;

import java.util.Set;

public interface I_ConflictAvoidanceTable {

    /**
     * Removes current conflicts and other information related to the agent in the given {@link SingleAgentPlan}.
     * Adds current conflicts and other information related to the given {@link SingleAgentPlan}.
     * @param singleAgentPlan a new {@link SingleAgentPlan}. The {@link SingleAgentPlan#agent} may already have a plan
     *                        in the table.
     */
    void add(SingleAgentPlan singleAgentPlan);

    /**
     * Selects the next {@link A_Conflict} that should be resolved.
     * @return the next {@link A_Conflict} that should be resolved.
     */
    A_Conflict selectConflict();

    /**
     * @return a deep copy of this class.
     */
    I_ConflictAvoidanceTable copy();

}
