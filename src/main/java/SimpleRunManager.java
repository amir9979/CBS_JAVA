import Experiments.GridExperiment;
import IO_Package.IO_Manager;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.OnlineInstanceBuilder_BGU;
import Instances.Maps.MapDimensions;
import Solvers.AStar.AStar_Solver;
import Solvers.AStar.SingleAgentAStar_Solver;
import Solvers.PrioritisedPlanning.OnlinePP_Solver;
import Solvers.PrioritisedPlanning.PrioritisedPlanning_Solver;
import Solvers.AStar.SingleAgentAStar_Solver;
import Solvers.I_Solver;
import Solvers.PrioritisedPlanning.PrioritisedPlanning_Solver;

import java.util.ArrayList;

public class SimpleRunManager extends A_RunManager {

    InstanceProperties properties = new InstanceProperties(new MapDimensions(512,512),-1f,new int[]{50});

    @Override
    void setSolvers() {
        this.solvers.add(new SingleAgentAStar_Solver());
        this.solvers.add(new PrioritisedPlanning_Solver(new SingleAgentAStar_Solver()));
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


        /*  =   Set Properties   =  */
        int numOfInstances = 20;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_BGU(),properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager,numOfInstances));

    }

    void addOnlineExperiment1(){

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                "converted - 1 agent every timestep"});
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);

        /*  =   Set Properties   =  */
        int numOfInstances = 17;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path,
                new OnlineInstanceBuilder_BGU(), properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager, "1 agent every timestep", numOfInstances));
    }

    void addOnlineExperiment2(){

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                "converted - 20percent agent arrival rate"});
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);

        /*  =   Set Properties   =  */

        int numOfInstances = 17;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path,
                new OnlineInstanceBuilder_BGU(), properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager, "20percent agent arrival rate", numOfInstances));
    }

    void addOnlineExperiment3(){

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                "converted - 80percent agent arrival rate"});
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);

        /*  =   Set Properties   =  */
        int numOfInstances = 17;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path,
                new OnlineInstanceBuilder_BGU(), properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager, "80percent agent arrival rate", numOfInstances));
    }
}
