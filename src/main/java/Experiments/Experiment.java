package Experiments;

import Instances.InstanceManager;
import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;
import Solvers.I_Solver;
import Solvers.RunParameters;
import Solvers.Solution;

public  class Experiment {

  public final String experimentName;
  public final int numOfInstances;
  protected InstanceManager instanceManager;

  public Experiment(String experimentName, InstanceManager instanceManager){
    this.experimentName = experimentName;
    this.instanceManager = instanceManager;
    this.numOfInstances = Integer.MAX_VALUE;
  }


  public Experiment(String experimentName, InstanceManager instanceManager, int numOfInstances) {
    this.experimentName = experimentName;
    this.instanceManager = instanceManager;
    this.numOfInstances = numOfInstances;
  }


  public InstanceReport setReport(MAPF_Instance instance, I_Solver solver){
    InstanceReport instanceReport = S_Metrics.newInstanceReport();
    /*  = Put values in report =  */
    instanceReport.putStringValue(InstanceReport.StandardFields.experimentName, this.experimentName);
    instanceReport.putStringValue(InstanceReport.StandardFields.mapName, instance.name);
    instanceReport.putIntegerValue(InstanceReport.StandardFields.numAgents, instance.agents.size());
    instanceReport.putStringValue(InstanceReport.StandardFields.solver, solver.getClass().getSimpleName());

    return instanceReport;
  }


  public void runExperiment(I_Solver solver) {

    if( solver == null ){ return; }

    for (int i = 0; i < this.numOfInstances; i++) {

      MAPF_Instance instance = instanceManager.getNextInstance();

      if (instance == null) {
        break;
      }

      RunParameters runParameters = new RunParameters(this.setReport(instance, solver));

      System.out.println("solving "  + instance.name);
      Solution solution = solver.solve(instance, runParameters);
      System.out.println("Solution: " + solution);
      System.out.println("Solution is " + (solution.isValidSolution() ? "valid!" : "invalid!!!"));
    }


  }


}