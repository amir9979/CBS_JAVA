package Instances;


public interface I_InstanceBuilder {

    MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath);

    InstanceManager.InstancePath[] getInstancesPaths(String directoryPath);

}
