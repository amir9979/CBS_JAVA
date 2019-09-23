package Solvers;

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
     */
    public final long timeout;

    /**
     * An unmodifiable list of {@link MoveConstraint move constraints} for the {@link I_Solver sovler} to use.
     * These constraints limit the {@link Move moves} that {@link Instances.Agents.Agent agents} can make. A
     * {@link I_Solver solver} that uses this field should start its solution process with these constraints, but may
     * later add or remove constraints, depending on the algorithm being used.
     */
    public final List<MoveConstraint> moveConstraints;

    /**
     * An unmodifiable list of {@link LocationConstraint location constraints} for the {@link I_Solver sovler} to use.
     * These constraints limit the {@link Instances.Maps.I_MapCell locations(map cells)} that
     * {@link Instances.Agents.Agent agents} may occupy. A {@link I_Solver solver} that uses this field should start its
     * solution process with these constraints, but may later add or remove constraints, depending on the algorithm
     * being used.
     */
    public final List<LocationConstraint> locationConstraints;

    /*  =Constructors=  */

    public RunParameters(long timeout, List<MoveConstraint> moveConstraints, List<LocationConstraint> locationConstraints) {
        this.timeout = timeout;
        this.moveConstraints = moveConstraints == null ? null : List.copyOf(moveConstraints);
        this.locationConstraints = locationConstraints == null ? null : List.copyOf(locationConstraints);
    }

    public RunParameters(List<MoveConstraint> moveConstraints, List<LocationConstraint> locationConstraints) {
        this(defaultTimeout, moveConstraints, locationConstraints);
    }

    public RunParameters() {
        this(null, null);
    }

}
