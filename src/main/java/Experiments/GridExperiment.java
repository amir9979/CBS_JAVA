package Experiments;

import Instances.InstanceManager;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;

import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

public class GridExperiment extends A_Experiment {

    public final Integer numOfInstances;

    public GridExperiment(InstanceManager instanceManager, String experimentName, int numOfInstances) {
        super(instanceManager, experimentName);
        this.numOfInstances = numOfInstances;
    }

    public GridExperiment(InstanceManager instanceManager, int numOfInstances) {
        super(instanceManager);
        this.numOfInstances = numOfInstances;
    }

    @Override
    public void runExperiment(I_Solver solver) {

        for (int i = 0; i < this.numOfInstances; i++) {

            MAPF_Instance instance = instanceManager.getNextInstance();

            if( instance == null ){
                break;
            }

            InstanceReport instanceReport = S_Metrics.newInstanceReport();

            instanceReport.putStringValue(InstanceReport.StandardFields.experimentName, super.experimentName);
            instanceReport.putStringValue(InstanceReport.StandardFields.instanceName,instance.name);

            Solution solution = solver.solve(instance,new RunParameters(instanceReport));
            // Todo - what to do with solution
            System.out.println(solution);
        }
    }


}
