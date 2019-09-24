package Solvers;

import Instances.Agents.Agent;

import java.util.*;

/**
 * A collection of {@link SingleAgentPlan}s, representing a solution to a Path Finding problem.
 * If the collection contains more than one plan, it is a solution to a Multi Agent Path Finding problem.
 */
public class Solution implements Iterable<SingleAgentPlan>{
    /**
     * An unmodifiable {@link Map}, mapping {@link Agent agents} to their {@link SingleAgentPlan plans}.
     */
    private final Map<Agent, SingleAgentPlan> agentPlans;

    public Solution(Map<Agent, SingleAgentPlan> agentPlans) {
        this.agentPlans = Map.copyOf(agentPlans);
    }

    public Solution(Collection<SingleAgentPlan> plans) {
        Map<Agent, SingleAgentPlan> agentPlanMap = new HashMap<>();
        for (SingleAgentPlan plan :
                plans) {
            agentPlanMap.put(plan.agent, plan);
        }
        this.agentPlans = agentPlanMap;
    }

    public SingleAgentPlan getPlanFor(Agent agent){
        return agentPlans.get(agent);
    }

    /*  = Iterator Interface =  */

    @Override
    public Iterator<SingleAgentPlan> iterator() {
        return agentPlans.values().iterator();
    }

    @Override
    public String toString() {
        return agentPlans.values().toString();
        //nicetohave JSON string or something
    }

    //nicetohave validations?
}
