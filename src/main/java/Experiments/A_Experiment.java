package Experiments;

import Instances.InstanceManager;

import java.util.List;

public abstract class A_Experiment {
   private InstanceManager instanceManager;
//   private List<Instance> instances;

    public A_Experiment(int numOfAgents, double obstacleRate, int boardHeight, int BoardWidth, int numOfInstances ) {
    }

    private void generateProblem(int instanceIndex){
    }

    public abstract void runExperiment();

//    public I_instance getInstancesSafeCopy(){
//
//    }

}