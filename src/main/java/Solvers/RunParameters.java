package Solvers;

import Metrics.InstanceReport;
import Solvers.ConstraintsAndConflicts.Constraint;

import java.util.List;

/**
 * A set of parameters for a {@link I_Solver solver} to use when solving an {@link Instances.MAPF_Instance instance}.
 * All parameters can be null or invalid, so {@link I_Solver solvers} should validate all fields before using them, and
 * provide default values if possible. When using this class, {@link I_Solver solvers} don't have to use all the fields,
 * as some fields may not be relevant to some solvers.
 */
public class RunParameters {
    /*  =Constants=  */
    private static final long defaultTimeout = 1000*60*5 /*5 minutes*/;

    /*  =Fields=  */
    /**
     * The maximum time (milliseconds) allotted to the search. If the search exceeds this time, it is aborted.
     * Can also be 0, or negative.
     */
    public final long timeout;

    /**
     * An unmodifiable list of {@link Constraint location constraints} for the {@link I_Solver sovler} to use.
     * A {@link I_Solver solver} that uses this field should start its solution process with these constraints, but may
     * later add or remove constraints, depending on the algorithm being used. @Nullable
     */
    public final List<Constraint> constraints;

    /**
     * An {@link InstanceReport} where to {@link I_Solver} will write metrics generated from the run.
     * Can be null.
     */
    public final InstanceReport instanceReport;

    /*  =Constructors=  */

    public RunParameters(long timeout, List<Constraint> constraints, InstanceReport instanceReport) {
        this.timeout = timeout;
        this.constraints = constraints == null ? null : List.copyOf(constraints);
        this.instanceReport = instanceReport;
    }

    public RunParameters(List<Constraint> constraints, InstanceReport instanceReport) {
        this(defaultTimeout, constraints, instanceReport);
    }

    public RunParameters(InstanceReport instanceReport) {
        this(null, instanceReport);
    }


    public RunParameters() {
        this(null, null);
    }

}
