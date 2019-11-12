import Experiments.Experiment;
import Instances.InstanceManager;
import Instances.MAPF_Instance;
import Solvers.I_Solver;
import java.util.ArrayList;
import java.util.List;

public abstract class A_RunManager {

    protected List<I_Solver> solvers = new ArrayList<>();
    protected List<Experiment> experiments = new ArrayList<>();

    abstract void setSolvers();
    abstract void setExperiments();

    public void runAllExperiments(){

        setSolvers();
        setExperiments();

        for ( Experiment experiment : experiments ) {
            for ( I_Solver solver : solvers ) {

                experiment.runExperiment(solver);
            }
        }


        System.out.println("RunAllExperiments - Done!");
    }


    public static MAPF_Instance getInstanceFromPath(InstanceManager manager, InstanceManager.InstancePath absolutePath){
        return manager.getSpecificInstance(absolutePath);
    }


}
