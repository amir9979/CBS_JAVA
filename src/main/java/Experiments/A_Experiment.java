package Experiments;

import Instances.InstanceManager;
import Solvers.I_Solver;

public abstract class A_Experiment {
 public String experimentName;

 protected InstanceManager instanceManager;

 public A_Experiment(InstanceManager instanceManager, String experimentName) {
  this.instanceManager = instanceManager;
  this.experimentName = experimentName;
 }

 public A_Experiment(InstanceManager instanceManager) {
  this(instanceManager, "Unnamed Experiment");
 }

 public abstract void runExperiment(I_Solver solver);


}