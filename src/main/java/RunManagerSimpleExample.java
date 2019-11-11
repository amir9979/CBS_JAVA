import Experiments.GridExperiment;
import IO_Package.IO_Manager;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.Maps.MapDimensions;
import Solvers.CBS.CBS_Solver;


public class RunManagerSimpleExample extends A_RunManager {



    /*  = Set Solvers =  */

    @Override
    void setSolvers() {
        this.solvers.add(new CBS_Solver());
    }




    /*  = Set Experiments =  */

    @Override
    void setExperiments() {
        addExperiment_16_7();
    }



    private void addExperiment_16_7(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                "Instances"});


        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new MapDimensions(16,16), (float)0, new int[]{7});
        int numOfInstances = 1;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_BGU(),properties);

        /*  =   Add new experiment   =  */
        GridExperiment gridExperiment = new GridExperiment(instanceManager,numOfInstances);
        this.experiments.add(gridExperiment);
    }




}
