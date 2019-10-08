import Experiments.GridExperiment;
import IO_Package.IO_Manager;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Solvers.AStar.AStar_Solver;
import Solvers.AStar.SingleAgentAStar_Solver;
import Solvers.I_Solver;
import Solvers.PrioritisedPlanning.PrioritisedPlanning_Solver;

import java.util.ArrayList;

public class SimpleRunManager extends A_RunManager {


    @Override
    void setSolvers() {
        this.solvers.add(new PrioritisedPlanning_Solver(new SingleAgentAStar_Solver()));
    }

    @Override
    void setExperiments() {

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                "Instances"});


        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new int[]{16,16}, (float)0, new int[]{7},"-");
        int numOfInstances = 1;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_BGU(),properties);

        /*  =   Add new experiment   =  */
        this.experiments.add(new GridExperiment(instanceManager,numOfInstances));

    }
}
