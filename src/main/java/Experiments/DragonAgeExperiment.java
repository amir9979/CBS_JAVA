package Experiments;

import Instances.InstanceManager;
import Metrics.InstanceReport;
import Metrics.S_Metrics;

public class DragonAgeExperiment extends A_Experiment {


    public DragonAgeExperiment(InstanceManager instanceManager){
        super(instanceManager);
    }

    public DragonAgeExperiment(InstanceManager instanceManager, int numOfInstances) {
        super(instanceManager, numOfInstances);
    }


    @Override
    public InstanceReport setReport(){
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        /*  = Put values in report =  */
        instanceReport.putStringValue(InstanceReport.StandardFields.experimentName, "DragonAge Experiment");

        return instanceReport;
    }


}
