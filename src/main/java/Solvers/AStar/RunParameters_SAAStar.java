package Solvers.AStar;

import Metrics.InstanceReport;
import Solvers.ConstraintsAndConflicts.ConstraintSet;
import Solvers.RunParameters;
import Solvers.Solution;

public class RunParameters_SAAStar extends RunParameters {
    /**
     * A heuristic function to use for this run.
     */
    public AStarHeuristic heuristicFunction;
    public int problemStartTime = 0;

    public RunParameters_SAAStar(long timeout, ConstraintSet constraints, InstanceReport instanceReport, Solution existingSolution, AStarHeuristic heuristicFunction) {
        super(timeout, constraints, instanceReport, existingSolution);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(ConstraintSet constraints, InstanceReport instanceReport, Solution existingSolution, AStarHeuristic heuristicFunction) {
        super(constraints, instanceReport, existingSolution);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(ConstraintSet constraints, InstanceReport instanceReport, AStarHeuristic heuristicFunction) {
        super(constraints, instanceReport);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(InstanceReport instanceReport, Solution existingSolution, AStarHeuristic heuristicFunction) {
        super(instanceReport, existingSolution);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(InstanceReport instanceReport, AStarHeuristic heuristicFunction) {
        super(instanceReport);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(ConstraintSet constraints, AStarHeuristic heuristicFunction) {
        super(constraints);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(Solution existingSolution, AStarHeuristic heuristicFunction) {
        super(existingSolution);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(long timeout, AStarHeuristic heuristicFunction) {
        super(timeout);
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(AStarHeuristic heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    public RunParameters_SAAStar(RunParameters runParameters, AStarHeuristic heuristicFunction) {
        this(runParameters.timeout, runParameters.constraints, runParameters.instanceReport, runParameters.existingSolution, heuristicFunction);
    }

}
