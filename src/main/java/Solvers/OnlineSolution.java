package Solvers;

import Instances.Agents.Agent;

import java.util.Map;

public class OnlineSolution extends Solution{

    public final Map<Integer, Solution> solutionsAtTimes;

    public OnlineSolution(Map<Integer, Solution> solutionsAtTimes) {
        //make unified solution for super
        super(mergeSolutions(solutionsAtTimes));
        this.solutionsAtTimes = solutionsAtTimes;
    }

    private static Map<Agent, SingleAgentPlan> mergeSolutions(Map<Integer, Solution> solutionsAtTimes) {
        return null;   //imp
    }
}
