package Solvers.PrioritisedPlanning;

import Instances.Agents.Agent;
import Instances.MAPF_Instance;
import Solvers.I_Solver;
import Solvers.MoveConstraint;
import Solvers.RunParameters;
import Solvers.Solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class PrioritisedPlanning_Solver implements I_Solver {

    private long maximumRuntime;
    /**
     * An array of {@link Agent}s to plan for, ordered by priority (descending).
     */
    private List<Agent> agents;

    private List<MoveConstraint> moveConstraints;

    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        releaseMemory();
        return null; //imp
    }

    /**
     * Initialises the object in preparation to solving an {@link MAPF_Instance}.
     * @param instance - the instance that we will have to solve.
     * @param runParameters - parameters that affect the solution process.
     */
    protected void init(MAPF_Instance instance, RunParameters runParameters){
        if(instance == null || runParameters == null){throw new IllegalArgumentException();}

        maximumRuntime = (runParameters.timeout >= 0) ? runParameters.timeout : 5*60*1000;
        agents = new ArrayList<>(instance.agents);
        //todo initial constraints

        if(runParameters instanceof RunParameters_PP){
            RunParameters_PP parameters = (RunParameters_PP)runParameters;

            //reorder according to requested priority

            Agent[] requestedOrder = parameters.preferredPriorityOrder;
            HashSet<Agent> tmpAgents = new HashSet<>(this.agents);
            this.agents.clear();

            for (Agent orderedAgent: //add by order
                 requestedOrder) {
                if(tmpAgents.contains(orderedAgent)){
                    this.agents.add(orderedAgent);
                    tmpAgents.remove(orderedAgent);
                }
            }
            this.agents.addAll(tmpAgents); //add remaining agents not found in the requested order collection.

        }
    }

    /**
     * Clears local fields, to allow the garbage collector to clear the memory that is no longer in use.
     * All fields should be cleared by this method. Any data that might be relevant later on should be passed as part
     * of the {@link Solution} that is output by {@link #solve(MAPF_Instance, RunParameters)}, or written to an {@link Metrics.InstanceReport}.
     */
    private void releaseMemory() {
        //imp
    }
}
