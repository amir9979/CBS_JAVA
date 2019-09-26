package Solvers.PrioritisedPlanning;

import Instances.Agents.Agent;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;
import Solvers.*;
import Solvers.ConstraintsAndConflicts.Constraint;
import Solvers.ConstraintsAndConflicts.ConstraintSet;

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
    private ConstraintSet constraints;
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

    /**
     * Constructor.
     * @param lowLevelSolver A {@link I_Solver solver}, to be used for solving sub-problems where only one agent is to
     *                      be planned for, and the existing {@link Solvers.SingleAgentPlan plans} for other
     *                      {@link Agent}s are to be avoided.
     */
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
        this.constraints = runParameters.constraints == null ? new ConstraintSet()
                : new ConstraintSet(runParameters.constraints);
        this.instanceReport = runParameters.instanceReport == null ? S_Metrics.newInstanceReport()
                : runParameters.instanceReport;

        if(runParameters instanceof RunParameters_PP){
            RunParameters_PP parameters = (RunParameters_PP)runParameters;

            //reorder according to requested priority
            if(parameters.preferredPriorityOrder != null) {reorderAgentsByPriority(parameters.preferredPriorityOrder);}
        }
    }

    protected void reorderAgentsByPriority(Agent[] requestedOrder) {
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

    protected Solution solvePrioritisedPlanning() {
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
            RunParameters subproblemParameters = new RunParameters(this.constraints.getOriginalConstraints(), subproblemReport);

            //solve sub-problem
            SingleAgentPlan planForAgent = lowLevelSolver.solve(subproblem, subproblemParameters).getPlanFor(currentAgent);
            try {
                subproblemReport.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //save
            agentPlans.put(currentAgent, planForAgent);
            //add constraints to prevent the next agents from conflicting with the new plan
            constraints.addAll(swappingConstraintsForPlan(planForAgent));
            constraints.addAll(vertexConstraintsForPlan(planForAgent));
        }

        endTime = System.currentTimeMillis();
        return new Solution(agentPlans);
    }

    protected List<Constraint> vertexConstraintsForPlan(SingleAgentPlan planForAgent) {
        List<Constraint> constraints = new LinkedList<>();
        for (Move move :
                planForAgent) {
            constraints.add(new Constraint(null, move.timeNow, move.currLocation));
        }
        return constraints;
    }

    protected List<Constraint> swappingConstraintsForPlan(SingleAgentPlan planForAgent) {
        List<Constraint> constraints = new LinkedList<>();
        for (Move move :
                planForAgent) {
            constraints.add(new Constraint(null, move.timeNow,
                    /*the constraint is in opposite direction of the move*/ move.currLocation, move.prevLocation));
        }
        return constraints;
    }

    /*  = wind down =  */

    protected void writeMetricsToReport(Solution solution) {
        instanceReport.putIntegerValue(InstanceReport.StandardFields.solved, abortedForTimeout ? 1 : 0);
        instanceReport.putStingValue(InstanceReport.StandardFields.startTime, new Date(endTime).toString());
        instanceReport.putIntegerValue(InstanceReport.StandardFields.elapsedTimeMS, (int)(endTime-startTime));
        instanceReport.putStingValue(InstanceReport.StandardFields.solution, solution.toString());
    }

    /**
     * Clears local fields, to allow the garbage collector to clear the memory that is no longer in use.
     * All fields should be cleared by this method. Any data that might be relevant later should be passed as part
     * of the {@link Solution} that is output by {@link #solve(MAPF_Instance, RunParameters)}, or written to an {@link Metrics.InstanceReport}.
     */
    protected void releaseMemory() {
        this.constraints = null;
        this.agents = null;
        this.instance = null;
        this.instanceReport = null;
    }
}
