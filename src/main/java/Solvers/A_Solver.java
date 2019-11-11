package Solvers;

import Instances.MAPF_Instance;
import Metrics.InstanceReport;
import Metrics.S_Metrics;

import java.io.IOException;
import java.util.Date;

/**
 * Performs that functionality that is common to all solvers.
 */
public abstract class A_Solver implements I_Solver{
    protected long DEFAULT_TIMEOUT = 5*60*1000; //5 minutes

    protected long maximumRuntime;
    protected InstanceReport instanceReport;
    protected boolean commitReport;

    protected long startTime;
    protected long endTime;
    protected boolean abortedForTimeout;
    protected int totalLowLevelStatesGenerated;
    protected int totalLowLevelStatesExpanded;

    /**
     * This implementation provides a skeleton for running a solver. You can override any of the invoked methods, but if
     * you do, it is recommended to also call the implementation defined in this class at some point during your implementation.
     * You can also completely override this method and implement a different workflow, while using the methods defined
     * in this class as services.
     * @param instance {@inheritDoc}
     * @param parameters {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        Solution solution = runAlgorithm(instance, parameters);
        writeMetricsToReport(solution);
        tryCommitReport();
        releaseMemory();
        return solution;
    }

    /*  = initialization =  */

    /**
     * Prepares for a run. Must initialize all fields, making sure that no data from a previous run pollutes this run.
     * @param instance an instance that we are about to solve.
     * @param parameters parameters for this coming run.
     */
    protected void init(MAPF_Instance instance, RunParameters parameters){
        if(instance == null || parameters == null){throw new IllegalArgumentException();}

        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.abortedForTimeout = false;
        this.totalLowLevelStatesGenerated = 0;
        this.totalLowLevelStatesExpanded = 0;
        this.maximumRuntime = (parameters.timeout >= 0) ? parameters.timeout : this.DEFAULT_TIMEOUT;
        this.instanceReport = parameters.instanceReport == null ? S_Metrics.newInstanceReport()
                : parameters.instanceReport;
        // if we were given a report, we should leave it be. If we created our report locally, then it is unreachable
        // outside the class, and should therefore be committed.
        this.commitReport = parameters.instanceReport == null;
    }

    /*  = algorithm =  */

    protected abstract Solution runAlgorithm(MAPF_Instance instance, RunParameters parameters);

    /*  = wind down =  */

    /**
     * Writes metrics about the run and the solution to {@link #instanceReport}.
     * @param solution
     */
    protected void writeMetricsToReport(Solution solution){
        instanceReport.putIntegerValue(InstanceReport.StandardFields.timeoutThresholdMS, (int) this.maximumRuntime);
        instanceReport.putStringValue(InstanceReport.StandardFields.startTime, new Date(startTime).toString());
        instanceReport.putIntegerValue(InstanceReport.StandardFields.elapsedTimeMS, (int)(endTime-startTime));
        if(solution != null){
            instanceReport.putStringValue(InstanceReport.StandardFields.solution, solution.toString());
            instanceReport.putIntegerValue(InstanceReport.StandardFields.solved, 1);
        }
        else{
            instanceReport.putIntegerValue(InstanceReport.StandardFields.solved, 0);
        }
        instanceReport.putIntegerValue(InstanceReport.StandardFields.generatedNodesLowLevel, this.totalLowLevelStatesGenerated);
        instanceReport.putIntegerValue(InstanceReport.StandardFields.expandedNodesLowLevel, this.totalLowLevelStatesExpanded);
    }

    /**
     * Commits the report if {@link #commitReport} is true.
     */
    protected void tryCommitReport(){
        if(commitReport){
            try {
                instanceReport.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Releases memory held by the solver.
     *
     * This frees as much memory as possible, so that running multiple solvers in succession would not cause later
     * solvers to slow down or fail.
     * This also helps make sure that successive runs on the same solver object would remain independent, though the
     * responsibility for this lies with {@link #init(MAPF_Instance, RunParameters)}.
     */
    protected void releaseMemory(){
        this.instanceReport = null;
    }

    /*  = utilities =  */

    protected boolean checkTimeout() {
        if(System.currentTimeMillis()-startTime > maximumRuntime){
            this.abortedForTimeout = true;
            return true;
        }
        return false;
    }
}
