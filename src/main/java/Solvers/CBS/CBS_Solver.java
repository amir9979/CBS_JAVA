package Solvers.CBS;

import Instances.Agents.Agent;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;
import Solvers.*;
import Solvers.AStar.DistanceTableAStarHeuristic;
import Solvers.AStar.RunParameters_SAAStar;
import Solvers.AStar.SingleAgentAStar_Solver;
import Solvers.ConstraintsAndConflicts.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CBS_Solver implements I_Solver {

    /*  = Fields =  */
    /*  =  = Fields related to the MAPF instance  =  */

    /*  =  = Fields related to the run =  */

    private MAPF_Instance instance;

    private long maximumRuntime;
    protected InstanceReport instanceReport;
    protected boolean commitReport;

    private long startTime;
    protected long endTime;
    private boolean abortedForTimeout;
    private int totalLowLevelStatesGenerated;
    private int totalLowLevelStatesExpanded;

    private DistanceTableAStarHeuristic aStarHeuristic;

    /*  =  = Fields related to the class instance =  */

    /**
     * A queue of open {@link CBS_Node nodes/states}. Also referred to as OPEN.
     */
    public final I_OpenList<CBS_Node> openList;
    /**
     * @see OpenListManagementMode
     */
    private final OpenListManagementMode openListManagementMode;
    /**
     * A {@link I_Solver solver}, to be used for solving single-{@link Instances.Agents.Agent agent} sub-problems.
     */
    private final I_Solver lowLevelSolver;
    /**
     * Cost may be more complicated than a simple SIC (Sum of Individual Costs), so retrieve it through this method.
     */
    private final CBSCostFunction costFunction;

    /*  = Constructors =  */

    /**
     * Parameterised constructor.
     * @param lowLevelSolver this {@link I_Solver solver} will be used to solve single agent sub-problems. @Nullable
     * @param openList this will be used as the {@link I_OpenList open list} in the solver. This instance will be reused
     *                 by calling {@link I_OpenList#clear()} after every run. @Nullable
     * @param openListManagementMode
     * @param costFunction a cost function for solutions.
     */
    public CBS_Solver(I_Solver lowLevelSolver, I_OpenList<CBS_Node> openList, OpenListManagementMode openListManagementMode, CBSCostFunction costFunction) {
        this.lowLevelSolver = Objects.requireNonNullElseGet(lowLevelSolver, SingleAgentAStar_Solver::new);
        this.openList = Objects.requireNonNullElseGet(openList, OpenList::new);
        this.openListManagementMode = openListManagementMode != null ? openListManagementMode : OpenListManagementMode.AUTOMATIC;
        clearOPEN();
        // if a specific cost function is not provided, use standard SIC (Sum of Individual Costs)
        this.costFunction = costFunction != null ? costFunction : (solution, cbs) -> solution.sumIndividualCosts();
    }

    /**
     * Default constructor.
     */
    public CBS_Solver() {
        this(null, null, null, null);
    }

    /*  = Interface Implementation =  */

    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        Solution solution = solveCBS(instance,
                Objects.requireNonNullElseGet(parameters.constraints, ConstraintSet::new));
        writeMetricsToReport(solution);
        releaseMemory();
        return solution;
    }

    /*  = initialization =  */

    private void init(MAPF_Instance instance, RunParameters runParameters) {
        if(instance == null || runParameters == null){throw new IllegalArgumentException();}

        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.abortedForTimeout = false;
        this.totalLowLevelStatesGenerated = 0;
        this.totalLowLevelStatesExpanded = 0;

        this.instance = instance;
        this.maximumRuntime = (runParameters.timeout >= 0) ? runParameters.timeout : 5*60*1000;
        this.instanceReport = runParameters.instanceReport == null ? S_Metrics.newInstanceReport()
                : runParameters.instanceReport;
        // if we were given a report, we should leave it be. If we created our report locally, then it is unreachable
        // outside the class, and should therefore be committed.
        this.commitReport = runParameters.instanceReport == null;

        this.aStarHeuristic = this.lowLevelSolver instanceof SingleAgentAStar_Solver ?
                new DistanceTableAStarHeuristic(new ArrayList<>(this.instance.agents), this.instance.map) :
                null;
    }

    /*  = algorithm =  */

    /**
     * Implements the CBS algorithm, as described in the original CBS article from Proceedings of the Twenty-Sixth AAAI
     * Conference on Artificial Intelligence.
     * @param instance the MAPF problem instance to solve.
     * @param initialConstraints a set of initial constraints on the agents. This may be modified as part of the run.
     * @return
     */
    private Solution solveCBS(MAPF_Instance instance, ConstraintSet initialConstraints) {
        initOpen(initialConstraints);
        CBS_Node goal = mainLoop();
        return solutionFromGoal(goal);
    }

    /**
     * Initialises the {@link #openList OPEN} and inserts the root.
     * @param initialConstraints a set of initial constraints on the agents.
     */
    private void initOpen(ConstraintSet initialConstraints) {
        if(this.openListManagementMode == OpenListManagementMode.AUTOMATIC ||
                this.openListManagementMode == OpenListManagementMode.AUTO_INIT_MANUAL_CLEAR){
            openList.add(initRoot(initialConstraints));
        }
    }

    /**
     * Creates a root node.
     */
    private CBS_Node initRoot(ConstraintSet initialConstraints) {
        Solution solution = new Solution(); // init an empty solution
        // for every agent, add its plan to the solution
        for (Agent agent :
                this.instance.agents) {
            solution = solveSubproblem(agent, solution, initialConstraints);
        }

        return new CBS_Node(solution, costFunction.solutionCost(solution, this), initialConstraints);
    }

    /**
     * The main loop of the CBS algorithm. Expands and generates nodes.
     * @return the goal node, or null if a timeout occurs before it is found.
     */
    private CBS_Node mainLoop() {
        while(!openList.isEmpty() && !checkTimeout()){
            CBS_Node node = openList.poll();
            updateConflictAvoidanceTableIn(node);
            node.setSelectedConflict(node.conflictAvoidanceTable.selectConflict());
            if(isGoal(node)){
                return node;
            }
            else {
                expand(node);
            }
        }
        // will never get here, since an unsolvable instance will simply lead to an infinite loop and eventually a timeout
        return null;
    }

    /**
     * When a node is first generated, it is given the same {@link ConflictAvoidanceTable} as its parent. Only when that
     * node is later dequeued from {@link #openList}, will we update the table.
     * @param node a {@link CBS_Node node} that contains an out of date {@link ConflictAvoidanceTable}.
     */
    private void updateConflictAvoidanceTableIn(CBS_Node node) {
        if(node.parent != null){ // isn't root
            I_ConflictAvoidanceTable cat = node.getConflictAvoidanceTable();
            // the agent that was constrained in this node is the agent who's plan has changed.
            SingleAgentPlan thePlanThatChangedInThisNode = node.solution.getPlanFor(node.addedConstraint.agent);
            cat.add(thePlanThatChangedInThisNode);
        }
    }

    private boolean isGoal(CBS_Node node) {
        // no conflicts -> found goal
        return node.selectedConflict == null;
    }

    /**
     * Expands a {@link CBS_Node}.
     * @param node a node to expand.
     */
    private void expand(CBS_Node node) {
        Constraint[] constraints = node.selectedConflict.getPreventingConstraints();
        // make copies of data structures for left child, while reusing the parent's data structures on the right child.
        node.leftChild = initNode(node, constraints[0], true);
        node.rightChild = initNode(node, constraints[1], false);

        openList.add(node.leftChild);
        openList.add(node.rightChild);
    }

    /**
     * Since the creation of a new {@link CBS_Node node} is somewhat complicated, it is handled in its own method.
     * @param parent the new node's parent
     * @param constraint the constraint that we want to add in this node, before re-solving the agent that is constrained.
     * @param copyDatastructures for one child, we may be able to reuse the parent's data structures, instead of copying them.
     * @return a new {@link CBS_Node}.
     */
    private CBS_Node initNode(CBS_Node parent, Constraint constraint, boolean copyDatastructures) {
        Solution parentSolution = parent.solution;
        I_ConflictAvoidanceTable parentCAT = parent.getConflictAvoidanceTable();
        ConstraintSet parentConstraints = parent.getConstraints();
        if(copyDatastructures) {
            parentSolution = new Solution(parentSolution);
            parentCAT = parentCAT.copy();
            parentConstraints = new ConstraintSet(parentConstraints);
        }
        parentConstraints.add(constraint);

        //the low-level updates the solution, so this is a reference to the same object as parentSolution
        Solution updatedSolution = solveSubproblem(constraint.agent, parentSolution, parentConstraints);

        return new CBS_Node(updatedSolution, costFunction.solutionCost(updatedSolution, this), constraint, parentConstraints,
                parentCAT /*as is. will be updated if needed when popping from open*/, parent);
    }

    /**
     * Solves a single agent sub-problem.
     * @param agent
     * @param currentSolution
     * @param constraints
     * @return a solution to a single agent sub-problem. Typically the same object as currentSolution, after being modified.
     */
    private Solution solveSubproblem(Agent agent, Solution currentSolution, ConstraintSet constraints) {
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        RunParameters subproblemParameters = getSubproblemParameters(currentSolution, constraints, instanceReport);
        Solution subproblemSolution = this.lowLevelSolver.solve(this.instance.getSubproblemFor(agent), subproblemParameters);
        digestSubproblemReport(instanceReport);
        return subproblemSolution;
    }

    private RunParameters getSubproblemParameters(Solution currentSolution, ConstraintSet constraints, InstanceReport instanceReport) {
        RunParameters subproblemParametes = new RunParameters(constraints, instanceReport, currentSolution);
        if(this.lowLevelSolver instanceof SingleAgentAStar_Solver){ // upgrades to a better heuristic
            subproblemParametes = new RunParameters_SAAStar(subproblemParametes, this.aStarHeuristic);
        }
        return subproblemParametes;
    }

    private void digestSubproblemReport(InstanceReport subproblemReport) {
        Integer statesGenerated = subproblemReport.getIntegerValue(InstanceReport.StandardFields.generatedNodesLowLevel);
        this.totalLowLevelStatesGenerated += statesGenerated==null ? 0 : statesGenerated;
        Integer statesExpanded = subproblemReport.getIntegerValue(InstanceReport.StandardFields.expandedNodesLowLevel);
        this.totalLowLevelStatesExpanded += statesExpanded==null ? 0 : statesExpanded;
        //we consolidate the subproblem report into the main report, and remove the subproblem report.
        S_Metrics.removeReport(subproblemReport);
    }

    /**
     * Extracts a solution from a goal {@link CBS_Node node}.
     * @param goal a {@link CBS_Node} that we consider to be a goal node.
     * @return a solution from a goal {@link CBS_Node node}.
     */
    private Solution solutionFromGoal(CBS_Node goal) {
        if(goal == null){
            return null;
        }
        else{
            return goal.solution;
        }
    }

    /**
     * Clears OPEN
     */
    private void clearOPEN() {
        if(this.openListManagementMode == OpenListManagementMode.AUTOMATIC ||
                this.openListManagementMode == OpenListManagementMode.MANUAL_INIT_AUTO_CLEAR){
            openList.clear();
        }
    }

    private boolean checkTimeout() {
        if(System.currentTimeMillis()-startTime > maximumRuntime){
            this.abortedForTimeout = true;
            return true;
        }
        return false;
    }

    /*  = wind down =  */

    private void writeMetricsToReport(Solution solution) {
        instanceReport.putIntegerValue(InstanceReport.StandardFields.timeout, abortedForTimeout ? 1 : 0);
        instanceReport.putStringValue(InstanceReport.StandardFields.startTime, new Date(startTime).toString());
        instanceReport.putIntegerValue(InstanceReport.StandardFields.elapsedTimeMS, (int)(endTime-startTime));
        if(solution != null){
            instanceReport.putStringValue(InstanceReport.StandardFields.solution, solution.toString());
            instanceReport.putIntegerValue(InstanceReport.StandardFields.solved, 1);
        }
        else{
            instanceReport.putIntegerValue(InstanceReport.StandardFields.solved, 0);
        }
        instanceReport.putIntegerValue(InstanceReport.StandardFields.generatedNodesLowLevel, this.totalLowLevelStatesGenerated);
        instanceReport.putIntegerValue(InstanceReport.StandardFields.expandedNodesLowLevel, this.totalLowLevelStatesExpanded);
        if(commitReport){
            try {
                instanceReport.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears unneeded memory after the run.
     */
    private void releaseMemory() {
        clearOPEN();
        this.instance = null;
        this.instanceReport = null;
        this.aStarHeuristic = null;
    }


    /*  = internal classes and interfaces =  */


    /**
     * Cost may be more complicated than a simple SIC (Sum of Individual Costs), so it is factored out with this interface.
     */
    public interface CBSCostFunction{
        float solutionCost(Solution solution, CBS_Solver cbs);
    }

    /**
     * A data type for representing a single node in the CBS search tree.
     * Try to keep most logic in {@link CBS_Solver}, avoiding methods in this class.
     */
    private class CBS_Node{

        /*  =  = fields =  */

        /**
         * The solution in this node. For every non-root node, this solution is after rerouting (solving low level) an
         * agent to overcome a conflict.
         * Holds references to the same {@link Solvers.SingleAgentPlan plans} as in {@link #parent}, apart from the plan
         * of the re-routed agent.
         */
        private Solution solution;
        /**
         * The cost of the solution.
         */
        private float solutionCost;
        /**
         * The constraint that was added in this node (missing from {@link #parent}). Contained in {@link #constraints}.
         */
        private Constraint addedConstraint;
        /**
         * constraints to abide by in this node.
         */
        private ConstraintSet constraints;
        /**
         * All conflicts between {@link Instances.Agents.Agent agents} in {@link #solution}. For OPEN nodes, this is
         * identical to {@link #parent}'s conflictAvoidanceTable (copy or same object). When expanding the node, this is
         * updated, and a conflict is chosen.
         */
        private I_ConflictAvoidanceTable conflictAvoidanceTable;
        /**
         * A {@link A_Conflict conflict}, selected from {@link #conflictAvoidanceTable}, to be solved by new constraints
         * in child nodes.
         */
        private A_Conflict selectedConflict;

        /*  =  =  = CBS tree branches =  =  */

        /**
         * This node's parent node. This node's {@link #addedConstraint} solves {@link #parent}'s {@link #selectedConflict}.
         */
        private CBS_Node parent;
        /**
         * One of this node's child nodes. Solves this node's {@link #selectedConflict} in one way.
         */
        private CBS_Node leftChild;
        /**
         * One of this node's child nodes. Solves this node's {@link #selectedConflict} in one way.
         */
        private CBS_Node rightChild;

        /*  =  = constructors =  */

        /**
         * Root constructor.
         * @param solution an initial solution for all agents.
         * @param solutionCost the cost of the solution.
         * @param constraints constraints to abide by in this node.
         */
        public CBS_Node(Solution solution, float solutionCost, ConstraintSet constraints) {
            this.solution = solution;
            this.solutionCost = solutionCost;
            this.constraints = constraints;
            for (SingleAgentPlan plan:
                 solution) {
                this.conflictAvoidanceTable.add(plan);
            }
            this.parent = null;
        }

        /**
         * Non-root constructor.
         */
        public CBS_Node(Solution solution, float solutionCost, Constraint addedConstraint, ConstraintSet constraints, I_ConflictAvoidanceTable conflictAvoidanceTable, CBS_Node parent) {
            this.solution = solution;
            this.solutionCost = solutionCost;
            this.addedConstraint = addedConstraint;
            this.constraints = constraints;
            this.conflictAvoidanceTable = conflictAvoidanceTable;
            this.parent = parent;
        }

        /*  =  = when expanding a node =  */

        /**
         * Get the {@link I_ConflictAvoidanceTable} in this node, and modify it in-place.
         */
        public I_ConflictAvoidanceTable getConflictAvoidanceTable() {
            return conflictAvoidanceTable;
        }

        /**
         * Set the selected conflict. Typically done through delegation to {@link I_ConflictAvoidanceTable#selectConflict()}.
         * @param selectedConflict
         */
        public void setSelectedConflict(A_Conflict selectedConflict) {
            this.selectedConflict = selectedConflict;
        }

        /**
         * Set a reference to one of the generated child nodes when expanding this node.
         * @param leftChild One of this node's child nodes. Solves this node's {@link #selectedConflict} in one way.
         */
        public void setLeftChild(CBS_Node leftChild) {
            this.leftChild = leftChild;
        }

        /**
         * Set a reference to one of the generated child nodes when expanding this node.
         * @param rightChild One of this node's child nodes. Solves this node's {@link #selectedConflict} in one way.
         */
        public void setRightChild(CBS_Node rightChild) {
            this.rightChild = rightChild;
        }

        /*  =  = getters =  */

        public Solution getSolution() {
            return solution;
        }

        public float getSolutionCost() {
            return solutionCost;
        }

        public Constraint getAddedConstraint() {
            return addedConstraint;
        }

        public ConstraintSet getConstraints() {
            return constraints;
        }

        public A_Conflict getSelectedConflict() {
            return selectedConflict;
        }

        public CBS_Node getParent() {
            return parent;
        }

        public CBS_Node getLeftChild() {
            return leftChild;
        }

        public CBS_Node getRightChild() {
            return rightChild;
        }
    }


    /**
     * Modes for handling the initialization and clearing of {@link #openList OPEN}. The default mode of operation is
     * {@link #AUTOMATIC}.
     */
    public enum OpenListManagementMode{
        /**
         * Will handle OPEN automatically. This is the standard mode of operation. The solver will clear OPEN before and
         * after every run, and initialize OPEN at the start of every run with a single root {@link CBS_Node node}.
         */
        AUTOMATIC,
        /**
         * Will initialize OPEN automatically, but clearing it before or after a run will be controlled manually.
         * Note that this means the solver keeps part of its state after running. If you want to reuse the solver, you
         * have to manually handle the clearing of OPEN. If you keep references to many such solvers, this may adversely
         * affect available memory.
         */
        AUTO_INIT_MANUAL_CLEAR,
        /**
         * Will not initialize OPEN (assumes that it was already initialized), but will clear it after running.
         * It is not cleared before running. If it were to be cleared before running, manual initialization would be
         * impossible.
         */
        MANUAL_INIT_AUTO_CLEAR,
        /**
         * Will not initialize OPEN (assumes that it was already initialized).
         * Will not clear OPEN automatically.
         * Note that this means the solver keeps part of its state after running. If you want to reuse the solver, you
         * have to manually handle the clearing of OPEN. If you keep references to many such solvers, this may adversely
         * affect available memory.
         */
        MANUAL
    }
}
