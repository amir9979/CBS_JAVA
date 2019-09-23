package Solvers.PrioritisedPlanning;

import Instances.Agents.Agent;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;
import Solvers.*;

import java.io.IOException;
import java.util.*;

/**
 * An implementation of the Prioritised Planning algorithm for Multi Agent Path Finding.
 */
public class PrioritisedPlanning_Solver implements I_Solver {

    /*  = Fields =  */
    /*  =  = Fields related to the instance =  */
    /**
     * An array of {@link Agent}s to plan for, ordered by priority (descending).
     */
    private List<Agent> agents;
    private MAPF_Instance instance;

    /*  =  = Fields related to the run =  */

    private long maximumRuntime;
    private List<MoveConstraint> moveConstraints;
    private List<LocationConstraint> locationConstraints;
    private InstanceReport instanceReport;

    private long startTime;
    private long endTime;
    private boolean abortedForTimeout;

    /*  =  = Fields related to the class =  */

    /**
     * A {@link I_Solver solver}, to be used for solving sub-problems where only one agent is to be planned for, and the
     * existing {@link Solvers.SingleAgentPlan plans} for other {@link Agent}s are to be avoided.
     */
    private final I_Solver lowLevelSolver;

    /*  = Constructors =  */

    public PrioritisedPlanning_Solver(I_Solver lowLevelSolver) {
        if(lowLevelSolver == null){throw new IllegalArgumentException();}
        this.lowLevelSolver = lowLevelSolver;
    }

    /*  = Interface Implementation =  */

    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        Solution solution = solvePrioritisedPlanning();
        writeMetricsToReport(solution);
        releaseMemory();
        return solution;
    }

    /*  = initialization =  */

    /**
     * Initialises the object in preparation to solving an {@link MAPF_Instance}.
     * @param instance - the instance that we will have to solve.
     * @param runParameters - parameters that affect the solution process.
     */
    protected void init(MAPF_Instance instance, RunParameters runParameters){
        if(instance == null || runParameters == null){throw new IllegalArgumentException();}

        this.startTime = System.currentTimeMillis();
        this.abortedForTimeout = false;

        this.agents = new ArrayList<>(instance.agents);
        this.instance = instance;

        this.maximumRuntime = (runParameters.timeout >= 0) ? runParameters.timeout : 5*60*1000;
        this.moveConstraints = runParameters.moveConstraints == null ? new ArrayList<>()
                : new ArrayList<>(runParameters.moveConstraints);
        this.locationConstraints = runParameters.locationConstraints == null ? new ArrayList<>()
                : new ArrayList<>(runParameters.locationConstraints);
        this.instanceReport = runParameters.instanceReport == null ? S_Metrics.newInstanceReport()
                : runParameters.instanceReport;

        if(runParameters instanceof RunParameters_PP){
            RunParameters_PP parameters = (RunParameters_PP)runParameters;

            //reorder according to requested priority
            if(parameters.preferredPriorityOrder != null) {reorderAgentsByPriority(parameters.preferredPriorityOrder);}
        }
    }

    private void reorderAgentsByPriority(Agent[] requestedOrder) {
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

    /*  = algorithm =  */

    private Solution solvePrioritisedPlanning() {
        Map<Agent, SingleAgentPlan> agentPlans = new HashMap<>(this.agents.size());

        //solve for each agent while avoiding the plans of previous agents.
        for (int i = 0; i < this.agents.size(); i++) {
            if(System.currentTimeMillis()-startTime > maximumRuntime){
                this.abortedForTimeout = true;
                break;
            }
            Agent currentAgent = this.agents.get(i);

            //create a sub-problem
            MAPF_Instance subproblem = instance.getSubproblemFor(this.agents.get(i));
            InstanceReport subproblemReport = S_Metrics.newInstanceReport();
            subproblemReport.putStingValue("Parent Instance", this.instance.name);
            subproblemReport.putStingValue("Parent Solver", PrioritisedPlanning_Solver.class.getSimpleName());
            RunParameters subproblemParameters = new RunParameters(new ArrayList<>(this.moveConstraints),
                    new ArrayList<>(this.locationConstraints), subproblemReport);

            //solve sub-problem
            SingleAgentPlan planForAgent = lowLevelSolver.solve(subproblem, subproblemParameters).agentPlans.get(currentAgent);
            try {
                subproblemReport.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //save
            agentPlans.put(currentAgent, planForAgent);
            //add constraints to prevent the next agents from conflicting with the new plan
            moveConstraints.addAll(moveConstraintsForPlan(planForAgent));
            locationConstraints.addAll(locationConstraintsForPlan(planForAgent));
        }

        endTime = System.currentTimeMillis();
        return new Solution(agentPlans);
    }

    private List<LocationConstraint> locationConstraintsForPlan(SingleAgentPlan planForAgent) {
        List<LocationConstraint> constraints = new LinkedList<>();
        for (Move move :
                planForAgent.getMoves()) {
            constraints.add(new LocationConstraint(null, move.timeNow, move.currLocation));
        }
        return constraints;
    }

    private List<MoveConstraint> moveConstraintsForPlan(SingleAgentPlan planForAgent) {
        List<MoveConstraint> constraints = new LinkedList<>();
        for (Move move :
                planForAgent.getMoves()) {
            constraints.add(new MoveConstraint(null, move.timeNow,
                    /*the constraint is in opposite direction of the move*/ move.currLocation, move.prevLocation));
        }
        return constraints;
    }

    /*  = wind down =  */

    private void writeMetricsToReport(Solution solution) {
        instanceReport.putIntegerValue("Timeout", abortedForTimeout ? 1 : 0);
        instanceReport.putStingValue("start Time", new Date(endTime).toString());
        instanceReport.putIntegerValue("Time Elapsed (ms)", (int)(endTime-startTime));
        instanceReport.putStingValue("Solution", solution.toString());
    }

    /**
     * Clears local fields, to allow the garbage collector to clear the memory that is no longer in use.
     * All fields should be cleared by this method. Any data that might be relevant later should be passed as part
     * of the {@link Solution} that is output by {@link #solve(MAPF_Instance, RunParameters)}, or written to an {@link Metrics.InstanceReport}.
     */
    private void releaseMemory() {
        this.locationConstraints = null;
        this.moveConstraints = null;
        this.agents = null;
        this.instance = null;
        this.instanceReport = null;
    }
}
