package Experiments;

import Instances.InstanceManager;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

public abstract class A_Experiment {

  public final int numOfInstances;
  protected InstanceManager instanceManager;

  public A_Experiment(InstanceManager instanceManager){
    this.instanceManager = instanceManager;
    this.numOfInstances = Integer.MAX_VALUE;
  }


  public A_Experiment(InstanceManager instanceManager, int numOfInstances) {
    this.instanceManager = instanceManager;
    this.numOfInstances = numOfInstances;
  }


  public abstract InstanceReport setReport();


  public void runExperiment(I_Solver solver) {

    if( solver == null ){ return; }

    for (int i = 0; i < this.numOfInstances; i++) {

      MAPF_Instance instance = instanceManager.getNextInstance();

      if (instance == null) {
        break;
      }

      Solution solution = solver.solve(instance, new RunParameters(this.setReport()));

      // Todo - what to do with solution
      System.out.println(solution);
    }


  }


}