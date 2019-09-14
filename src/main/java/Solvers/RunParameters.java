package Solvers;

/**
 * A set of parameters for a {@link I_Solver solver} to use when solving an {@link Instances.MAPF_Instance instance}.
 * All parameters can be null or invalid, so {@link I_Solver solvers} should validate all fields before using them, and
 * provide default values if possible. When using this {@link I_Solver solvers} don't have to use all the fields, some
 * of them may not be relevant to some solvers.
 */
public class RunParameters {
    /**
     * The maximum time (milliseconds) allotted to the search. If the search exceeds this time, it is aborted.
     */
    long timeout;

    MoveConstraint[] moveConstraints;

    LocationConstraint[] locationConstraints;
}
