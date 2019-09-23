package Solvers;

import Instances.Agents.Agent;

import java.util.Map;

/**
 * A collection of {@link SingleAgentPlan}s, representing a solution to a Path Finding problem.
 * If the collection contains more than one plan, it is a solution to a Multi Agent Path Finding problem.
 */
public class Solution {
    /**
     * An unmodifiable {@link Map}, mapping {@link Agent agents} to their {@link SingleAgentPlan plans}.
     */
    public final Map<Agent, SingleAgentPlan> agentPlans;

    public Solution(Map<Agent, SingleAgentPlan> agentPlans) {
        this.agentPlans = Map.copyOf(agentPlans);
    }

    @Override
    public String toString() {
        //imp JSON string or something
        return agentPlans.toString();
    }

    //nicetohave validations?
}
