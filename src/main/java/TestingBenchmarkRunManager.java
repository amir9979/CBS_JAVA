import Experiments.Experiment;
import IO_Package.IO_Manager;
import Instances.InstanceBuilder_BGU;
import Instances.InstanceManager;
import Solvers.CBS.CBS_Solver;

public class TestingBenchmarkRunManager extends A_RunManager {

    @Override
    void setSolvers() {
        this.solvers.add( new CBS_Solver());
    }

    @Override
    void setExperiments() {

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                                                            "TestingBenchmark"});

        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_BGU());
        this.experiments.add(new Experiment("Test Benchmark", instanceManager));

    }
}
