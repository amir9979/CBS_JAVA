package Solvers.CBS;

import Instances.MAPF_Instance;
import Solvers.AStar.SingleAgentAStar_Solver;
import Solvers.ConstraintsAndConflicts.*;
import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

import java.util.Objects;

public class CBS_Solver implements I_Solver {


    /*  = Fields =  */
    /*  =  = Fields related to the instance =  */

    /*  =  = Fields related to the run =  */

    /*  =  = Fields related to the class =  */

    /**
     * A {@link I_Solver solver}, to be used for solving single-{@link Instances.Agents.Agent agent} sub-problems.
     */
    private final I_Solver lowLevelSolver;

    /*  = Constructors =  */

    public CBS_Solver(I_Solver lowLevelSolver) {
        this.lowLevelSolver = Objects.requireNonNullElseGet(lowLevelSolver, SingleAgentAStar_Solver::new);
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

    private void init(MAPF_Instance instance, RunParameters parameters) {
        //imp
    }

    /*  = algorithm =  */

    private Solution solveCBS(MAPF_Instance instance, ConstraintSet initialConstraints) {
        return null; //imp
    }

    /*  = wind down =  */

    private void writeMetricsToReport(Solution solution) {
        //imp
    }

    private void releaseMemory() {
        //imp
    }


    /*  = internal classes =  */

    /**
     * A data type for representing a single node in the CBS search tree.
     * Try to keep most logic in {@link CBS_Solver}, avoiding methods in this class.
     */
    private class CBS_Node{

        /*  = fields =  */

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

        /*  =  = CBS tree branches =  =  */

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

        /*  = constructors =  */

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

        /*  = when expanding a node =  */

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

        /*  = getters =  */

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
}
