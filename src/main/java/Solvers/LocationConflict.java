package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

/**
 * Represents a conflict between 2 {@link Agent}s, at a certain time, in a certain {@link I_MapCell location}.
 * This is known as a Vertex Conflict.
 */
public class LocationConflict {
    public final Agent agent1;
    public final Agent agent2;
    public final int time;
    public final I_MapCell location;

    public LocationConflict(Agent agent1, Agent agent2, int time, I_MapCell location) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.time = time;
        this.location = location;
    }

    /**
     * returns an array of {@link LocationConstraint}, each of which could prevent this conflict.
     * @return an array of {@link LocationConstraint}, each of which could prevent this conflict.
     */
    public LocationConstraint[] getPreventingConstraints(){
        return new LocationConstraint[]{
                new LocationConstraint(agent1,time, location),
                new LocationConstraint(agent2,time, location)};
    }
}
