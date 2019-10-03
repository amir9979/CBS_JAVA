package Experiments;

import Instances.InstanceManager;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;

import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

import java.io.IOException;

public class GridExperiment extends A_Experiment {

    private String EXPERIMENT_NAME;


    public GridExperiment(InstanceManager instanceManager, int numOfInstances, String experimentName) {
        super(instanceManager, numOfInstances);
        this.EXPERIMENT_NAME = experimentName;
    }

    @Override
    public void runExperiment(I_Solver solver) {

        for (int i = 0; i < this.numOfInstances; i++) {
            MAPF_Instance instance = instanceManager.getNextInstance();
            if(instance == null) {break;}

            InstanceReport instanceReport = S_Metrics.newInstanceReport();

            instanceReport.putStingValue(InstanceReport.StandardFields.experimentName, EXPERIMENT_NAME);
            instanceReport.putStingValue(InstanceReport.StandardFields.instanceName,instance.name);

            Solution solution = solver.solve(instance,new RunParameters(instanceReport));
//            instanceReport.putStingValue(InstanceReport.StandardFields.solution, solution.readableToString());
//            instanceReport.putStingValue(InstanceReport.StandardFields.solution, "");

            try {
                instanceReport.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
