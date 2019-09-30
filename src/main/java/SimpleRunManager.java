import Experiments.GridExperiment;
import IO_Package.IO_Manager;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.OnlineInstanceBuilder_BGU;
import Solvers.AStar.AStar_Solver;
import Solvers.AStar.SingleAgentAStar_Solver;
import Solvers.PrioritisedPlanning.OnlinePP_Solver;
import Solvers.PrioritisedPlanning.PrioritisedPlanning_Solver;

public class SimpleRunManager extends A_RunManager {


    @Override
    void setSolvers() {
        this.solvers.add(new AStar_Solver());
    }

    void addPrioritisedPlanningSolver(){
        this.solvers.add(new PrioritisedPlanning_Solver(new SingleAgentAStar_Solver()));
    }

    void addOnlinePrioritisedPlanningSolver(){
        this.solvers.add(new OnlinePP_Solver(new SingleAgentAStar_Solver()));
    }

    @Override
    void setExperiments() {

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                                                            "Instances"});
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);


        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new int[]{16,16},0,7,"-");
        int numOfInstances = 1;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_BGU(),properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager,numOfInstances, "no name"));

    }

    void addOnlineExperiment1(){

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new int[]{16,16},0,7,"-");
        int numOfInstances = 17;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager("C:\\Users\\John\\Google Drive\\Documents\\1Uni\\Thesis\\Code\\Online Instances BGU format\\converted - 1 agent every timestep",
                new OnlineInstanceBuilder_BGU(), properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager, numOfInstances, "1 agent every timestep"));
    }

    void addOnlineExperiment2(){

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new int[]{16,16},0,7,"-");
        int numOfInstances = 17;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager("C:\\Users\\John\\Google Drive\\Documents\\1Uni\\Thesis\\Code\\Online Instances BGU format\\converted - 20percent agent arrival rate",
                new OnlineInstanceBuilder_BGU(), properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager, numOfInstances, "20percent agent arrival rate"));
    }

    void addOnlineExperiment3(){

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new int[]{16,16},0,7,"-");
        int numOfInstances = 17;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager("C:\\Users\\John\\Google Drive\\Documents\\1Uni\\Thesis\\Code\\Online Instances BGU format\\converted - 80percent agent arrival rate",
                new OnlineInstanceBuilder_BGU(), properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager, numOfInstances, "80percent agent arrival rate"));
    }
}
