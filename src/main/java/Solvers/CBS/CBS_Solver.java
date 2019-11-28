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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class CBS_Solver extends A_Solver {

    /*  = Fields =  */

    private static Comparator<? super CBS_Node> nodeCostFunction = Comparator.comparing(CBS_Node::getSolutionCost);

    /*  =  = Fields related to the MAPF instance  =  */

    private MAPF_Instance instance;

    /*  =  = Fields related to the run =  */

    private DistanceTableAStarHeuristic aStarHeuristic;
    private ConstraintSet initialConstraints;
    private int generatedNodes;
    private int expandedNodes;

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

    /*  = initialization =  */

    @Override
    protected void init(MAPF_Instance instance, RunParameters runParameters) {
        super.init(instance, runParameters);
        this.initialConstraints = Objects.requireNonNullElseGet(runParameters.constraints, ConstraintSet::new);
        this.generatedNodes = 0;
        this.expandedNodes = 0;
        this.instance = instance;
        this.aStarHeuristic = this.lowLevelSolver instanceof SingleAgentAStar_Solver ?
                new DistanceTableAStarHeuristic(new ArrayList<>(this.instance.agents), this.instance.map) :
                null;
    }

    /*  = algorithm =  */

    /**
     * Implements the CBS algorithm, as described in the original CBS article from Proceedings of the Twenty-Sixth AAAI
     * Conference on Artificial Intelligence.
     * @param instance {@inheritDoc}
     * @param parameters {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Solution runAlgorithm(MAPF_Instance instance, RunParameters parameters) {
        initOpen(Objects.requireNonNullElseGet(parameters.constraints, ConstraintSet::new));
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
            addToOpen(generateRoot(initialConstraints));
        }
    }

    /**
     * Creates a root node.
     */
    private CBS_Node generateRoot(ConstraintSet initialConstraints) {
        this.generatedNodes++;

        Solution solution = new Solution(); // init an empty solution
        // for every agent, add its plan to the solution
        for (Agent agent :
                this.instance.agents) {
            solution = solveSubproblem(agent, solution, initialConstraints);
        }

        return new CBS_Node(solution, costFunction.solutionCost(solution, this));
    }

    /**
     * The main loop of the CBS algorithm. Expands and generates nodes.
     * @return the goal node, or null if a timeout occurs before it is found.
     */
    private CBS_Node mainLoop() {
        while(!openList.isEmpty() && !checkTimeout()){
            CBS_Node node = openList.poll();

            // verify solution (find conflicts)
            updateConflictAvoidanceTableIn(node);
            node.setSelectedConflict(node.conflictAvoidanceTable.selectConflict());

            if(isGoal(node)){
                return node;
            }
            else {
                expandNode(node);
            }
        }

        return null; //probably a timeout
    }

    /**
     * When a node is first generated, it is given the same {@link ConflictManager} as its parent. Only when that
     * node is later dequeued from {@link #openList}, will we update the table.
     * @param node a {@link CBS_Node node} that contains an out of date {@link ConflictManager}.
     */
    private void updateConflictAvoidanceTableIn(CBS_Node node) {
        if(node.parent != null){ // isn't root
            I_ConflictManager cat = node.getConflictAvoidanceTable();
            // the agent that was constrained in this node is the agent who's plan has changed.
            SingleAgentPlan thePlanThatChangedInThisNode = node.solution.getPlanFor(node.addedConstraint.agent);
            cat.addPlan(thePlanThatChangedInThisNode);
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
    private void expandNode(CBS_Node node) {
        this.expandedNodes++;

        Constraint[] constraints = node.selectedConflict.getPreventingConstraints();
        // make copies of data structures for left child, while reusing the parent's data structures on the right child.
        node.leftChild = generateNode(node, constraints[0], true);
        node.rightChild = generateNode(node, constraints[1], false);

        if(node.leftChild == null || node.rightChild == null){
            return; //probably a timeout in the low level. should abort.
        }
        addToOpen(node.leftChild);
        addToOpen(node.rightChild);
    }

    /**
     * Adds a node to {@link #openList OPEN}. If a duplicate node exists, keeps the one with less cost.
     * @param node a node to insert into {@link #openList OPEN}
     * @return true if {@link #openList OPEN} changed as a result of the call.
     */
    private boolean addToOpen(CBS_Node node) {
        return openList.add(node);
        // for duplicate detection, if needed:
//        if(!openList.contains(node)){
//            return openList.add(node);
//        }
//        else{ //keep the one with least cost
//            CBS_Node existingNode = openList.get(node);
//            CBS_Node keptNode = openList.keepOne(existingNode, node, CBS_Node::compareTo);
//            return keptNode.equals(node);
//        }
    }

    /**
     * Since the creation of a new {@link CBS_Node node} is somewhat complicated, it is handled in its own method.
     * @param parent the new node's parent
     * @param constraint the constraint that we want to add in this node, before re-solving the agent that is constrained.
     * @param copyDatastructures for one child, we may be able to reuse the parent's data structures, instead of copying them.
     * @return a new {@link CBS_Node}.
     */
    private CBS_Node generateNode(CBS_Node parent, Constraint constraint, boolean copyDatastructures) {
        this.generatedNodes++;

        Agent agent = constraint.agent;

        Solution solution = parent.solution;
        I_ConflictManager cat = parent.getConflictAvoidanceTable();

        // replace with copies if required
        if(copyDatastructures) {
            solution = new Solution(solution);
            cat = cat.copy();
        }

        // modify for this node
        /*  replace the current plan for the agent with an empty plan, so that the low level won't try to continue the
            existing plan.
            Also we don't want to reuse (modify) SingleAgentPlan objects, as they are pointed to by other Solution objects, which
            we don't want to modify.
         */
        solution.putPlan(new SingleAgentPlan(agent));

        //the low-level should update the solution, so this is a reference to the same object as solution. We do this to
        //reuse Solution objects instead of creating extra ones.
        Solution agentSolution = solveSubproblem(agent, solution, buildConstraintSet(parent, constraint));
        if(agentSolution == null) {
            return null; //probably a timeout
        }
        // in case the low-level didn't update the Solution object it was given, this makes sure we preserve other agents'
        // plans, and add the re-planned agent's new plan.
        solution.putPlan(agentSolution.getPlanFor(agent));

        return new CBS_Node(solution, costFunction.solutionCost(solution, this), constraint,
                cat /*as is. will be updated if needed when popping from open*/, parent);
    }

    /**
     * When solving a new node, you want a set of constraints that apply to it. To save on memory, this set is created
     * on the spot, by climbing up the CT and collecting all the constraints that were added
     * @param parentNode the new node's parent.
     * @param newConstraint the constraint that this new node adds.
     * @return a {@link ConstraintSet} of all the constraints from parentNode to the root, plus newConstraint.
     */
    private ConstraintSet buildConstraintSet(CBS_Node parentNode, Constraint newConstraint) {
        // start by adding all the constraints that we were asked to start the solver with (and are therefore not in the CT)
        ConstraintSet constraintSet = new ConstraintSet(this.initialConstraints);

        CBS_Node currentNode = parentNode;
        while (currentNode.addedConstraint != null){ // will skip the root (it has no constraints)
            constraintSet.add(currentNode.addedConstraint);
            currentNode = currentNode.parent;
        }
        constraintSet.add(newConstraint);
        return constraintSet;
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
        long timeLeftToTimeout = super.maximumRuntime - (System.currentTimeMillis() - super.startTime);
        RunParameters subproblemParametes = new RunParameters(timeLeftToTimeout, constraints, instanceReport, currentSolution);
        if(this.lowLevelSolver instanceof SingleAgentAStar_Solver){ // upgrades to a better heuristic
            subproblemParametes = new RunParameters_SAAStar(subproblemParametes, this.aStarHeuristic);
        }
        return subproblemParametes;
    }

    private void digestSubproblemReport(InstanceReport subproblemReport) {
        Integer statesGenerated = subproblemReport.getIntegerValue(InstanceReport.StandardFields.generatedNodesLowLevel);
        super.totalLowLevelStatesGenerated += statesGenerated==null ? 0 : statesGenerated;
        Integer statesExpanded = subproblemReport.getIntegerValue(InstanceReport.StandardFields.expandedNodesLowLevel);
        super.totalLowLevelStatesExpanded += statesExpanded==null ? 0 : statesExpanded;
        Integer lowLevelRuntime = subproblemReport.getIntegerValue(InstanceReport.StandardFields.elapsedTimeMS);
        super.instanceReport.integerAddition(InstanceReport.StandardFields.totalLowLevelTimeMS, lowLevelRuntime);
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

    /*  = wind down =  */

    @Override
    protected void writeMetricsToReport(Solution solution) {
        super.writeMetricsToReport(solution);
        super.instanceReport.putIntegerValue(InstanceReport.StandardFields.generatedNodes, this.generatedNodes);
        super.instanceReport.putIntegerValue(InstanceReport.StandardFields.expandedNodes, this.expandedNodes);
        if(solution != null){
            super.instanceReport.putStringValue(InstanceReport.StandardFields.solutionCostFunction, "SIC");
            super.instanceReport.putIntegerValue(InstanceReport.StandardFields.solutionCost, solution.sumIndividualCosts());
        }
    }

    @Override
    protected void releaseMemory() {
        clearOPEN();
        this.instance = null;
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
    private class CBS_Node implements Comparable<CBS_Node>{

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
         * The constraint that was added in this node (missing from {@link #parent}).
         */
        private Constraint addedConstraint;
        /**
         * All conflicts between {@link Instances.Agents.Agent agents} in {@link #solution}. For OPEN nodes, this is
         * identical to {@link #parent}'s conflictAvoidanceTable (copy or same object). When expanding the node, this is
         * updated, and a conflict is chosen.
         */
        private I_ConflictManager conflictAvoidanceTable;
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
         */
        public CBS_Node(Solution solution, float solutionCost) {
            this.solution = solution;
            this.solutionCost = solutionCost;
            this.conflictAvoidanceTable = new ConflictManager();
            for (SingleAgentPlan plan:
                 solution) {
                this.conflictAvoidanceTable.addPlan(plan);
            }
            this.parent = null;
        }

        /**
         * Non-root constructor.
         */
        public CBS_Node(Solution solution, float solutionCost, Constraint addedConstraint, I_ConflictManager conflictAvoidanceTable, CBS_Node parent) {
            this.solution = solution;
            this.solutionCost = solutionCost;
            this.addedConstraint = addedConstraint;
            this.conflictAvoidanceTable = conflictAvoidanceTable;
            this.parent = parent;
        }

        /*  =  = when expanding a node =  */

        /**
         * Get the {@link I_ConflictManager} in this node, and modify it in-place.
         */
        public I_ConflictManager getConflictAvoidanceTable() {
            return conflictAvoidanceTable;
        }

        /**
         * Set the selected conflict. Typically done through delegation to {@link I_ConflictManager#selectConflict()}.
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

        @Override
        public int compareTo(CBS_Node o) {
            return Objects.compare(this, o, CBS_Solver.nodeCostFunction);
        }

        // for duplicate detection, if needed:
//        /**
//         * Determined by {@link #constraints} only.
//         * @param o {@inheritDoc}
//         * @return {@inheritDoc}
//         */
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (!(o instanceof CBS_Node)) return false;
//
//            CBS_Node cbs_node = (CBS_Node) o;
//
//            return constraints.equals(cbs_node.constraints);
//
//        }
//
//        /**
//         * Determined by {@link #constraints} only.
//         * @return {@inheritDoc}
//         */
//        @Override
//        public int hashCode() {
//            return constraints.hashCode();
//        }
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
