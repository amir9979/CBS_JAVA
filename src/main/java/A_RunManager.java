import Experiments.A_Experiment;
import Solvers.I_Solver;
import java.util.ArrayList;
import java.util.List;

public abstract class A_RunManager {

    protected List<I_Solver> solvers = new ArrayList<>();
    protected List<A_Experiment> experiments = new ArrayList<>();

    abstract void setSolvers();
    abstract void setExperiments();

    public void runAllExperiments(){

        for ( A_Experiment experiment : experiments ) {
            for ( I_Solver solver : solvers ) {

                experiment.runExperiment(solver);
            }
        }
    }


}
