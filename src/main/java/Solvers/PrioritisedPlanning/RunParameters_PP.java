package Solvers.PrioritisedPlanning;

import Instances.Agents.Agent;
import Metrics.InstanceReport;
import Solvers.I_Solver;
import Solvers.LocationConstraint;
import Solvers.MoveConstraint;
import Solvers.RunParameters;

import java.util.List;

/**
 * {@link RunParameters} for {@link PrioritisedPlanning_Solver}.
 */
public class RunParameters_PP extends RunParameters {
    /**
     * The {@link PrioritisedPlanning_Solver} will use this as the priority of the {@link Agent}s, with lower index
     * {@link Agent}s being treated as having higher priority. In practise this means they will be planned for first,
     * and then avoided when planning for higher index {@link Agent}s.
     * If the {@link PrioritisedPlanning_Solver} is given an {@link Instances.MAPF_Instance} which contains agents not in
     * this collection, they will all be treated as having lower priority, and their internal order will be determined
     * arbitrarily. If this collection contains {@link Agent}s that are not in the {@link Instances.MAPF_Instance},
     * they will be ignored.
     */
    public final Agent[] preferredPriorityOrder;

    public RunParameters_PP(long timeout, List<MoveConstraint> moveConstraints, List<LocationConstraint> locationConstraints, InstanceReport instanceReport, Agent[] preferredPriorityOrder) {
        super(timeout, moveConstraints, locationConstraints, instanceReport);
        this.preferredPriorityOrder = preferredPriorityOrder;
    }

    public RunParameters_PP(List<MoveConstraint> moveConstraints, List<LocationConstraint> locationConstraints, InstanceReport instanceReport, Agent[] preferredPriorityOrder) {
        super(moveConstraints, locationConstraints, instanceReport);
        this.preferredPriorityOrder = preferredPriorityOrder;
    }

    public RunParameters_PP(InstanceReport instanceReport, Agent[] preferredPriorityOrder) {
        super(instanceReport);
        this.preferredPriorityOrder = preferredPriorityOrder;
    }

    public RunParameters_PP(Agent[] preferredPriorityOrder) {
        super();
        this.preferredPriorityOrder = preferredPriorityOrder;
    }

    public RunParameters_PP() {
        super();
        this.preferredPriorityOrder = new Agent[0];
    }
}
