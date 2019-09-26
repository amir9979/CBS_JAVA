package Solvers.AStar;

import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Solvers.ConstraintsAndConflicts.Constraint;
import Solvers.ConstraintsAndConflicts.ConstraintSet;
import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

import java.util.List;

public class AStar_Solver implements I_Solver {

    private long maximumRuntime;
    private List<Constraint> constraints;
    private InstanceReport instanceReport;

    /*
    imp:
    General:
    =====
    We need an A* solver that can solve path finding problems with heuristic search.

    Notable:
    =====
    An implementation of **single agent** A* already exists from the AI assignment last year. You can use parts from it.
    Use I_Coordinate.EuclideanDistance for a heuristic.

    Demands:
    ======
    Must implement I_Solver interface.
    Must support planning with constraints. It gets these from the instance of RunParameters that it gets in Solve() (see I_Solver, see RunParameters).
    Must also be able to solve MAPF_Instances with only 1 agent.
    Must be stateless/reusable, meaning I can use the same instance of this class over and over, without worrying about one use affecting the next one.
     */

    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        Solution solution = solveAStar();
        writeMetricsToReport(solution);
        releaseMemory();
        return solution;
    }


    protected void init(MAPF_Instance instance, RunParameters runParameters){
        //imp - set local fields in preparation for a run
    }

    protected Solution solveAStar() {
        return null; //imp
    }

    protected void writeMetricsToReport(Solution solution) {
        //imp - write some metrics about the run
    }

    protected void releaseMemory() {
        //imp - clear local fields
    }

}
