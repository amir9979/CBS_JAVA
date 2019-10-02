package Experiments;

import Instances.InstanceManager;
import Solvers.I_Solver;
public abstract class A_Experiment {



 protected InstanceManager instanceManager;

 public A_Experiment(InstanceManager instanceManager) {
  this.instanceManager = instanceManager;
 }

 public abstract void runExperiment(I_Solver solver);


}