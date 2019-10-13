package Solvers.CBS;

import Instances.MAPF_Instance;
import Solvers.AStar.SingleAgentAStar_Solver;
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
        Solution solution = solveCBS(instance);
        writeMetricsToReport(solution);
        releaseMemory();
        return solution;
    }

    /*  = initialization =  */

    private void init(MAPF_Instance instance, RunParameters parameters) {
        //imp
    }

    /*  = algorithm =  */

    private Solution solveCBS(MAPF_Instance instance) {
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

    private class CBS_Node{
        //imp
    }
}
