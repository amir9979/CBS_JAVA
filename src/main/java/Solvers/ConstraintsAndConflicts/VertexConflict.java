package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

/**
 * Represents a conflict between 2 {@link Agent}s, at a certain time, in a certain {@link I_MapCell location}.
 * This is known as a Vertex Conflict.
 */
public class VertexConflict extends A_Conflict {


    public VertexConflict(Agent agent1, Agent agent2, int time, I_MapCell location) {
        super(agent1, agent2, time, location);
    }

    /**
     * returns an array of {@link Constraint}, each of which could prevent this conflict.
     * @return an array of {@link Constraint}, each of which could prevent this conflict.
     */
    public Constraint[] getPreventingConstraints(){
        return new Constraint[]{
                new Constraint(agent1,time, location),
                new Constraint(agent2,time, location)};
    }

}
