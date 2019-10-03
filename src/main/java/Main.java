import IO_Package.IO_Manager;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.MAPF_Instance;

public class Main {

    // Todo - set notes in all projects
    // imp
    // done
    // testme
    // blocking



    public static void main(String[] args) {

        simpleRun();

        moragRequestedRun();



    }




    public static void simpleRun(){

        SimpleRunManager simpleRunManager = new SimpleRunManager();
        simpleRunManager.setSolvers();
        simpleRunManager.setExperiments();
        simpleRunManager.runAllExperiments();
    }


    public static void moragRequestedRun(){


        MoragRequested_RunManager moragRequested_runManager = new MoragRequested_RunManager();
        moragRequested_runManager.setSolvers();
        moragRequested_runManager.setExperiments();

        moragRequested_runManager.runAllExperiments();



        /* Get Instance by absolute path */

        String path = IO_Manager.buildPath(
                new String[]{   IO_Manager.testResources_Directory,
                        "Instances\\\\Instance-8-15-5-17 - hard one - cost 29 and some corridors"}
        );
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);

        InstanceManager manager = new InstanceManager(new InstanceBuilder_BGU());


        MAPF_Instance instance = moragRequested_runManager.getInstanceWithAbsolutePath(manager, instancePath);
    }

}
