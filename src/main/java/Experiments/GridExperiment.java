package Experiments;

import Instances.InstanceManager;
import Metrics.InstanceReport;
import Metrics.S_Metrics;

public class GridExperiment extends A_Experiment {


    public GridExperiment(InstanceManager instanceManager){
        super(instanceManager);
    }

    public GridExperiment(InstanceManager instanceManager, int numOfInstances) {
        super(instanceManager, numOfInstances);
    }


    @Override
    public InstanceReport setReport(){
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        /*  = Put values in report =  */
        instanceReport.putStringValue(InstanceReport.StandardFields.experimentName, "Grid Experiment");

        return instanceReport;
    }


}
