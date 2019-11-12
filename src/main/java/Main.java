import IO_Package.IO_Manager;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.MAPF_Instance;
import Solvers.CBS.CBS_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

public class Main {

    // Todo - set notes in all projects
    // imp
    // done
    // testme
    // blocking



    public static void main(String[] args) {

        runTestingBenchmarkExperiment();

        // runExample();
        // runMultipleExperimentsExample();
    }



    public static void runTestingBenchmarkExperiment(){
        TestingBenchmarkRunManager testingBenchmarkRunManager = new TestingBenchmarkRunManager();
        testingBenchmarkRunManager.runAllExperiments();
    }


    public static void runMultipleExperimentsExample(){
        RunManagerSimpleExample runManagerSimpleExample = new RunManagerSimpleExample();
        runManagerSimpleExample.runAllExperiments();
    }


    public static void runExample(){

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                        "Instances\\\\Instance-8-15-5-17 - hard one - cost 29 and some corridors"}
        );
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);


        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(null, (float)0.15, new int[]{5});


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(null, new InstanceBuilder_BGU(),properties);

        MAPF_Instance instance = RunManagerSimpleExample.getInstanceFromPath(instanceManager, instancePath);

        // Solve
        CBS_Solver solver = new CBS_Solver();
        RunParameters runParameters = new RunParameters();
        Solution solution = solver.solve(instance, runParameters);

        //print
        System.out.println(solution);
    }



}
