package Experiments;

import Instances.InstanceManager;
import Instances.InstanceProperties;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;

import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

import java.util.List;

public class GridExperiment extends A_Experiment {

    private static final String EXPERIMENT_NAME = "Grid Experiment";


    public GridExperiment(InstanceManager instanceManager, int numOfInstances) {
        super(instanceManager, numOfInstances);

    }

    @Override
    public void runExperiment(I_Solver solver) {

        for (int i = 0; i < this.numOfInstances; i++) {
            MAPF_Instance instance = instanceManager.getNextInstance();

            InstanceReport instanceReport = S_Metrics.newInstanceReport();

            instanceReport.putStingValue(InstanceReport.StandardFields.experimentName, EXPERIMENT_NAME);
            instanceReport.putStingValue(InstanceReport.StandardFields.instanceName,instance.name);

            Solution solution = solver.solve(instance,new RunParameters(instanceReport));
            // Todo - what to do with solution
        }
    }


}
