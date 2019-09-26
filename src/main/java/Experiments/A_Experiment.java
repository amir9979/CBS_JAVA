package Experiments;

import Instances.InstanceManager;
import Solvers.I_Solver;
public abstract class A_Experiment {



 protected InstanceManager instanceManager;
 public final int numOfInstances;

 public A_Experiment(InstanceManager instanceManager, int numOfInstances) {
  this.instanceManager = instanceManager;
  this.numOfInstances = numOfInstances;

 }

 private void generateProblem(int instanceIndex){

 }

 public abstract void runExperiment(I_Solver solver);



 public void getInstances_safeCopy(){

 }

}