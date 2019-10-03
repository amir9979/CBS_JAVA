import Experiments.GridExperiment;
import IO_Package.IO_Manager;
import Instances.*;
import Solvers.AStar.AStar_Solver;
import Solvers.Solution;

public class MoragRequested_RunManager extends A_RunManager {


    @Override
    void setSolvers() {
        this.solvers.add(new AStar_Solver());

    }

    @Override
    void setExperiments() {


        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                                                            "Instances"});


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManagerMissingProperties = new InstanceManager(path, new InstanceBuilder_BGU(), null);

        /*  =   Add new experiment   =  */
        int unknownValue = Integer.MAX_VALUE;
        this.experiments.add(new GridExperiment(instanceManagerMissingProperties, unknownValue));


    }


    MAPF_Instance getInstanceWithAbsolutePath(InstanceManager manager, InstanceManager.InstancePath absolutePath){
        return manager.getSpecificInstance(absolutePath);
    }
}
